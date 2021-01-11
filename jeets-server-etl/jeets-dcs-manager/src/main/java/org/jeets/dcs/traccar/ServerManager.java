package org.jeets.dcs.traccar;

import java.util.Map;

import org.jeets.traccar.NettyServer;
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
     * in Spring to be handled by Camel and Netty/Starter.
     * 
     * @param setupFile
     * 
     * @param beanFactory for Bean registration in application context
     * @throws Exception
     */
    private void setupTraccarServers(ConfigurableListableBeanFactory beanFactory) throws Exception {
        long start = System.currentTimeMillis();

        Map<Integer, Class<?>> protocolClasses = TraccarSetup.loadProtocolClasses();
        LOG.info("found {} classes configured in configFile", protocolClasses.size());

    	Map<String, NettyServer> servers = TraccarSetup.prepareServers(protocolClasses);
    	LOG.info("created {} servers for {} protocols", servers.size(), protocolClasses.size());

    	if (servers.size() > 0) {
            for (String protocolSpec : servers.keySet()) {
            	NettyServer server = servers.get(protocolSpec);
//          	String routeBeanName = protocolSpec + "Bean";
//              Bean name is irrelevant, not referenced
//              registered to instantiate new TraccarRoute with Consumer uri
//              when: Apache Camel 3.3.0 (CamelContext: camel-1) is starting
            	beanFactory.registerSingleton(protocolSpec, server.factory);
                String uri = "netty:" + server.transport + "://" + host + ":" + server.port
                		+ "?serverInitializerFactory=#" + protocolSpec + "&sync=false";
                beanFactory.registerSingleton(protocolSpec + "-route", new TraccarRoute(uri, protocolSpec));
                LOG.info("registered {} server \t{}", protocolSpec, uri);
            }
        }
//      now server and serverInitFactory are registered, but Camel has not started!
//      see comment below and on BindException in Main class
        LOG.info("Setup {} Traccar servers in {} millis - ready for Camel to start!", 
                protocolClasses.size(), (System.currentTimeMillis() - start));
//      Camel start occurs later in Spring:
//      Apache Camel 3.3.0 (CamelContext: camel-1) is starting
//      Creating shared NettyConsumerExecutorGroup with 9 threads
//      ServerBootstrap binding to 0.0.0.0:5200
//      Netty consumer  bound  to: 0.0.0.0:5200
//      Route: jeetsRoute started and consuming from: netty://tcp://0.0.0.0:5200
//      see comment on BindException in Main class
    }

//  add get/setters for host ! Individual hosts for different protocols (?)

    /**
     * camel-netty and/or spring are/is tedious about localhost, which doesn't
     * accept external access (in ubuntu). On the remote system 0.0.0.0 should be
     * used instead of 127.0.0.1.
     */
    private String host = "0.0.0.0";

}
