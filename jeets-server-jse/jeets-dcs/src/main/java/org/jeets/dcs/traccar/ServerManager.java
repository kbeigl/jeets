package org.jeets.dcs.traccar;

import java.util.Set;

import org.jeets.traccar.routing.TraccarRoute;
import org.jeets.traccar.routing.TraccarSetup;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.BindResult;
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
         * explicitly load traccar.setupFile property (default: traccar.xml). Method can
         * also be extended to load an explicit *Properties class
         * with @ConfigurationProperties with: .bind("traccar.setupFile",
         * TraccarProperties.class) see stackoverflow.com/questions/61343153
         */
        BindResult<String> bindResult = Binder.get(environment).bind("traccar.setupfile", String.class);
        String setupFile = bindResult.get();
        LOG.info("traccar.setupfile: " + setupFile);

        try {
            setupTraccarServers(setupFile, beanFactory);
        } catch (Exception e) { 
            // TODO handle other cause/s than getProtocolPort contextInit Exception!
            LOG.error("Traccar Server setup failed: " + e.getMessage());
            e.printStackTrace();
//          traccar code: throw new RuntimeException("Configuration file is not provided");
//          System.exit(0); // don't apply at dev time and handle with care !!  
        }
    }

    /**
     * Setup the Traccar servers with ports defined in the setup file and register
     * them in Spring to be handled by Camel and Netty (starter).
     * @param setupFile
     * 
     * @param beanFactory for Bean registration in application context
     * @throws Exception 
     */
    private void setupTraccarServers(String setupFile, ConfigurableListableBeanFactory beanFactory) throws Exception {

//      Traccar Context is mandatory (hard coded in *Protocol classes!)
//      TraccarSetup.contextInit("./setup/traccar.xml");
        TraccarSetup.contextInit(setupFile);

        /*
         * Scanning only takes place when starting up the application, executes only
         * once. This could be optimized by scanning at Maven build time. see
         * www.baeldung.com/reflections-library
         * 
         * INFO Reflections took 477 ms to scan 2 urls, producing 11 keys and 754 values
         */
        Reflections reflections = new Reflections("org.traccar.protocol");
        Set<Class<? extends BaseProtocol>> protocolClasses = reflections.getSubTypesOf(BaseProtocol.class);

        String protocol = null;
        int port = -1;
        for (Class<? extends BaseProtocol> clazz : protocolClasses) {
//          see BaseProtocol.nameFromClass(class);
            String className = clazz.getSimpleName(); // TeltonikaProtocol
            protocol = className.substring(0, className.length() - 8).toLowerCase();

            port = TraccarSetup.getProtocolPort(protocol);
            if (port != -1) {

                beanFactory.registerSingleton(protocol, // TeltonikaProtocol.class
                        TraccarSetup.createServerInitializerFactory(clazz));

//              register netty as jeets-dcs ;)
                String uri = "netty:tcp://" + host + ":" + port 
                        + "?serverInitializerFactory=#" + protocol 
                        + "&sync=" + camelNettySync;
//              "&workerPool=#sharedPool&usingExecutorService=false" register in XML,

                beanFactory.registerSingleton(protocol + "Route", 
                        new TraccarRoute(uri, protocol));

            } else {
                LOG.debug("port# for " + protocol + " is not defined in configuration file. " + className + " Server is not launched!");
            }
        }
    }

//  add get/setters for host ! Individual hosts for different protocols (?)

    /**
     * The Consumer Endpoint (from) for each Traccar protocol must be set to false!
     * <p>
     * The Traccar Pipeline and -Decoders are implemented WITH ACK response, i.e.
     * channel.writeAndFlush. Therefore the Camel Endpoint, i.e. NettyConsumer,
     * should NOT return a (additional) response.
     * <p>
     * Note that this boolean variable is attached to the URI as String 'true' /
     * 'false'. Maybe apply String for type safety.
     */
    private boolean camelNettySync = false;
    /**
     * camel-netty and/or spring are/is tedious about localhost, which doesn't
     * accept external access (in ubuntu). On the remote system 0.0.0.0 should be
     * used instead of 127.0.0.1.
     */
    private String host = "0.0.0.0";
}
