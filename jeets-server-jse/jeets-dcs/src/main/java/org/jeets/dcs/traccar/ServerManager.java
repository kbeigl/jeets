package org.jeets.dcs.traccar;

import org.jeets.traccar.TraccarRoute;
import org.jeets.traccar.TraccarSetup;
//import org.reflections.Reflections;
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
import org.traccar.TrackerServer;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

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
         * also be widened to load an explicit *Properties class
         * with @ConfigurationProperties with: .bind("traccar.setupFile",
         * TraccarProperties.class) see stackoverflow.com/questions/61343153
         */
        BindResult<String> bindResult = Binder.get(environment).bind("traccar.setupfile", String.class);
        String setupFile = bindResult.get();
        LOG.info("using traccar.setupfile: " + setupFile);

        try {
//          move contextInit(setupFile) here ?
            setupTraccarServers(setupFile, beanFactory);
        } catch (Exception e) { 
            // TODO handle other cause/s than getProtocolPort contextInit Exception!
            LOG.error("Traccar Server setup failed: " + e.getMessage());
            e.printStackTrace();
//          traccar code: throw new RuntimeException("Configuration file is not provided");
//          System.exit(0); // don't apply at dev time and handle with care !!  
//          keep running for non Traccar Servers inside DCS Manager ! exit in protocols-traccar
        }
        
//      TODO setup other servers (jeets-protocol, netty En/Decoders etc.)
        
    }

    /**
     * Setup the Traccar servers with ports defined in the setup file and register
     * them in Spring to be handled by Camel and Netty (starter).
     * @param setupFile
     * 
     * @param beanFactory for Bean registration in application context
     * @throws Exception 
     */
//  TODO move to traccar project AND add tests (while stand alone is removed?)
    private void setupTraccarServers(String setupFile, ConfigurableListableBeanFactory beanFactory) throws Exception {

//      Traccar Context is mandatory (hard coded in *Protocol classes!)
//      TraccarSetup.contextInit("./setup/traccar.xml");
        TraccarSetup.contextInit(setupFile);

//      TODO move to protocols traccar project ----------------------
        
        long start = System.currentTimeMillis();
        try (
                ScanResult result = new ClassGraph()
                .acceptPackages("org.traccar.protocol") // with subpackages
                .acceptJars("jeets*.jar") // scan only jeets* sources !!
                .scan();
        ) {
            ClassInfoList classInfos = result.getSubclasses("org.traccar.BaseProtocol").directOnly();
            LOG.info("Found " + classInfos.size() + " BaseProtocol classes "
                    + "in " + (System.currentTimeMillis() - start) + " millis");

            String protocolName = null;
            int port = -1;
            for (ClassInfo protocolClassInfo : classInfos) {

                String className = protocolClassInfo.getSimpleName(); // TeltonikaProtocol
                protocolName = className.substring(0, className.length() - 8).toLowerCase();
                port = TraccarSetup.getProtocolPort(protocolName);

                if (port == -1) {
                    LOG.debug("port# for '" + protocolName + "' protocol is not defined in configuration file.");
                } else {

                    Class<?> clazz = protocolClassInfo.loadClass();
                    LOG.info("protocol name: " + protocolName + " class: " + protocolClassInfo 
                            + " loaded for port#" + port);

//                  private static BaseProtocol instantiateProtocol(Class<?> protocolClass) {
                    BaseProtocol protocolInstance = null;
                    try {
//                      invoke BaseProtocol constructor, initialize ..
                        protocolInstance = (BaseProtocol) clazz.newInstance();
//                      assertion
//                      System.out.println("protocolName: " + protocolInstance.getName());

                    } catch (InstantiationException | IllegalAccessException e) {
                        LOG.error(protocolClassInfo + " could not be instantiated!");
                        e.printStackTrace();
                    }

//                  public static ServerInitializerFactory createServerInitializerFactory(Class<?> protocolClass) {
                    String transport = "tcp";
                    TrackerServer server = TraccarSetup.getProtocolServer(transport, protocolInstance);
                    // compose URI and attach to server.setCamelUri() !
                    if (server == null) {
                        LOG.warn("No server found for '" + transport + ":" + protocolName);
//                      return null;  ??
                    }
//                  return server.getServerInitializerFactory();

                    beanFactory.registerSingleton(protocolName, // TeltonikaProtocol.class
                            server.getServerInitializerFactory());
//                          TraccarSetup.createServerInitializerFactory(clazz));

//                  register netty as jeets-dcs ;)
                    String uri = "netty:tcp://" + host + ":" + port 
                            + "?serverInitializerFactory=#" + protocolName 
                            + "&sync=" + camelNettySync;
//                  "&workerPool=#sharedPool&usingExecutorService=false" register in XML,

                    beanFactory.registerSingleton(protocolName + "Route", // teltonikaRoute
                            new TraccarRoute(uri, protocolName));

                }
            }
        }
    }
//      -----------------------------------------------------------------------


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
