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
 * entries (protocolname.port) in Context.Config to setup the Protocol server.
 * <p>
 * Context.Config does not provide the Properties object to traverse the *.port
 * entries to find protocol classes by protocol name. The ServerManager methods
 * to list classes of a package either for directories or jar files does not
 * work for nested SpringBoot jars. Neither does the ClassFinder implementation
 * from torsten horn.
 */
@Configuration
public class ServerManager implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(ServerManager.class);

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
        LOG.info("using traccar.setupfile: {}", traccarSetupFile);

        try {
//          if (setupFile is bad) skip setupTraccarServers
//          Traccar Context is mandatory (hard coded in *Protocol classes!)
//          TraccarSetup.contextInit("./setup/traccar.xml");
            TraccarSetup.contextInit(traccarSetupFile);
            setupTraccarServers(beanFactory);
            
//          then setup other servers in any case .. (here?)
            
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
        Map<Integer, Class<?>> protocolClasses = TraccarSetup.getConfiguredBaseProtocolClasses();
        int protocolClassesSize = protocolClasses.size();
        if (protocolClassesSize > 0) {

            LOG.info("found {} classes configured in configFile", protocolClassesSize);

            for (int port : protocolClasses.keySet()) {
                @SuppressWarnings("unchecked")
                Class<? extends BaseProtocol> clazz = (Class<? extends BaseProtocol>) protocolClasses.get(port);
                String className = clazz.getSimpleName(); // TeltonikaProtocol > teltonika
                String protocolName = className.substring(0, className.length() - 8).toLowerCase();
                
                ServerInitializerFactory pipeline = 
                        TraccarSetup.createServerInitializerFactory(clazz);
                beanFactory.registerSingleton(protocolName, pipeline);
                String uri = "netty:tcp://" + host + ":" + port 
                        + "?serverInitializerFactory=#" + protocolName + "&sync=false";
                LOG.info("added server {}", uri);

//              Bean name is irrelevant, not referenced
                String routeBeanName = protocolName + "Bean";
//              registered to instantiate new TraccarRoute with Consumer uri
//              when: Apache Camel 3.3.0 (CamelContext: camel-1) is starting
                beanFactory.registerSingleton(routeBeanName, new TraccarRoute(uri, protocolName));
                LOG.info("registerd @{} with {}", routeBeanName, protocolName + "Route");
            }

            LOG.info("Setup {} Traccar BaseProtocol servers in {} millis", 
                    protocolClassesSize, (System.currentTimeMillis() - start));
        
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

    /**
     * The Consumer Endpoint (from) for each Traccar protocol must be set to false!
     * <p>
     * The Traccar Pipeline and -Decoders are implemented WITH ACK response, i.e.
     * channel.writeAndFlush. Therefore the Camel Endpoint, i.e. NettyConsumer,
     * should NOT return a (additional) response.
     * <p>
     * Note that this boolean variable is attached to the URI as String 'true' /
     * 'false'. Maybe apply String for type safety.
    private boolean camelNettySync = false;
     */
}
