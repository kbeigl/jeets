package org.jeets.dcs.traccar;

import java.util.Map;

import org.apache.camel.component.netty.ServerInitializerFactory;
import org.jeets.traccar.TraccarRoute;
import org.jeets.traccar.TraccarSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.traccar.BaseProtocol;

/**
 * This class is modeled after and replaces Traccar's ServerManager only with
 * Spring and camel-netty server management by composing URI Strings.
 * <p>
 * The org.traccar.ServerManager logic first reads all eligible
 * *Protocol.classes. Then it traverses the classes, checks for *name*Protocol
 * entries &ltprotocolname.port> in Context.Config to setup the Protocol server.
 * <p>
 * Currently the ServerManager starts Traccar Protocols, Jeets Protocols
 * compiled into Traccar and/or separately and a sample of a Netty En/Decoder.
 */
@Configuration
public class ServerManager implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(ServerManager.class);

//  TODO: BeanFactoryPostProcessor javadoc:
//  See PropertyResourceConfigurer and its concrete implementations 
//  for out-of-the-box solutions that address such configuration needs.
    private Environment environment;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * The method BeanFactoryPostProcessor.postProcessBeanFactory is called by
     * Spring startup process just after all bean definitions have been loaded,
     * **but no beans have been instantiated yet**, i.e. @Bean definitions.
     * <p>
     * Spring boot internally uses Binder APIs to "map" the resolved properties into
     * the @ConfigurationProperties beans. This resolution happens during the spring
     * boot startup process AFTER the BeanFactoryPostProcessors get created. <br>
     * Therefore the Binder API is applied EnvironmentAware to load the properties
     * explicitly.
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        /*
         * explicitly load single traccar.setupFile property (default: traccar.xml).
         * Method can also be widened to load an explicit *Properties class
         * with @ConfigurationProperties with: .bind("traccar.setupFile",
         * TraccarProperties.class) see stackoverflow.com/questions/61343153
         */
        String traccarSetupFile = Binder.get(environment).bind("traccar.setupfile", String.class).get();
        LOG.debug("using traccar.setupfile: {}", traccarSetupFile);

        try {
//          if (setupFile is bad) > see catch
            TraccarSetup.contextInit(traccarSetupFile);
            setupTraccarServers(beanFactory);
            
//          then setup other servers in any case .. 
            
        } catch (Exception e) { 
//          this catch is not Traccar specific
//          TODO handle other cause/s than getProtocolPort contextInit Exception!
            LOG.error("Traccar Server setup failed: {}", e.getMessage());
            e.printStackTrace();
//          traccar code: throw new RuntimeException("Configuration file is not provided");
//          System.exit(0); // don't apply at dev time and handle with care !!  
//          keep running for non Traccar Servers inside DCS Manager ! exit in protocols-traccar
        }
        
//      TODO setup other servers (jeets-protocol, netty En/Decoders etc.)
        
    }

    /**
     * Setup Traccar servers with ports defined in the setup file and register them
     * in Spring to be handled by Camel and Netty (starter).
     * 
     * @param setupFile
     * 
     * @param beanFactory for Bean registration in application context
     * @throws Exception
     */
    private void setupTraccarServers(ConfigurableListableBeanFactory beanFactory) throws Exception {

        long start = System.currentTimeMillis();
        Map<Integer, Class<?>> protocolClasses = TraccarSetup.loadProtocolClasses();
        int protocolClassesSize = protocolClasses.size();
        if (protocolClassesSize > 0) {

            LOG.debug("found {} classes configured in configFile", protocolClassesSize);

            for (int port : protocolClasses.keySet()) {
                @SuppressWarnings("unchecked")
                Class<? extends BaseProtocol> clazz = (Class<? extends BaseProtocol>) protocolClasses.get(port);
                String className = clazz.getSimpleName(); // TeltonikaProtocol > teltonika
                String protocolName = className.substring(0, className.length() - 8).toLowerCase();

                ServerInitializerFactory pipeline = 
                        TraccarSetup.createServerInitializerFactory(clazz);
// ------------------------------------
                Map<String, ServerInitializerFactory> serverInitializerFactories = 
                		TraccarSetup.createServerInitializerFactories(clazz);

                for (Map.Entry<String, ServerInitializerFactory> factory : serverInitializerFactories.entrySet()) {

                    String protocolSpec = protocolName + "-" + factory.getKey(); // append transport
                    System.out.println(protocolSpec + ": " + factory.getValue());
                    
//                  pipeline can be registered with Camel ..
                    context.getRegistry().bind(protocolSpec, factory.getValue());
//                  .. or SpringBoot: @Bean(name = protocolName)
//                  beanFactory.registerSingleton(protocolName, pipeline);

//                  register netty as jeets-dcs ;)
                    String uri = "netty:" + factory.getKey() + "://" + host + ":" + port
                    		+ "?serverInitializerFactory=#" + protocolSpec + "&sync=false";
//                  		  "&workerPool=#sharedPool&usingExecutorService=false" etc.

                    context.addRoutes(new TraccarRoute(uri, protocolSpec)); // id=teltonikaRoute
//                  SpringBoot: @Bean(name = protocolName + "Route")
//                  beanFactory.registerSingleton(protocolName + "Route", new TraccarRoute(uri, protocolName));

                    LOG.info("added server: " + uri);
                }
// ------------------------------------
                beanFactory.registerSingleton(protocolName, pipeline);
                /*
                 * The Consumer Endpoint (from) for each Traccar protocol must be set to
                 * sync=false! The Traccar Pipeline and -Decoders are implemented WITH ACK
                 * response, i.e. channel.writeAndFlush. Therefore the Camel Endpoint, i.e.
                 * NettyConsumer, should NOT return a (additional) response.
                 */
                String uri = "netty:tcp://" + host + ":" + port 
                        + "?serverInitializerFactory=#" + protocolName + "&sync=false";
                LOG.info("registered {} server \t{}", protocolName, uri);

//              Bean name is irrelevant, not referenced
                String routeBeanName = protocolName + "Bean";
//              registered to instantiate new TraccarRoute with Consumer uri
//              when: Apache Camel 3.3.0 (CamelContext: camel-1) is starting
                beanFactory.registerSingleton(routeBeanName, new TraccarRoute(uri, protocolName));
                LOG.debug("registerd @{} with {}", routeBeanName, protocolName + "Route");
                
//              now server and serverInitFactory are registered, but Camel has not started!
//              see comment below and on BindException in Main class
            }

            LOG.info("Setup {} Traccar BaseProtocol servers in {} millis - ready for Camel to start!", 
                    protocolClassesSize, (System.currentTimeMillis() - start));
//          Camel start occurs later in Spring:
//          Apache Camel 3.3.0 (CamelContext: camel-1) is starting
//          Creating shared NettyConsumerExecutorGroup with 9 threads
//          ServerBootstrap binding to 0.0.0.0:5200
//          Netty consumer  bound  to: 0.0.0.0:5200
//          Route: jeetsRoute started and consuming from: netty://tcp://0.0.0.0:5200
//          see comment on BindException in Main class
        
        } else {
            LOG.warn("No classes found, which are configured in configFile");
        }
            
    }

//  add get/setters for host ! Individual hosts for different protocols (?)

    /**
     * camel-netty and/or spring are/is tedious about localhost, which doesn't
     * accept external access (in ubuntu). On the remote system 0.0.0.0 should be
     * used instead of 127.0.0.1.
     */
    private String host = "0.0.0.0";

}
