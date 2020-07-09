package org.jeets.dcs.traccar;

import java.util.Set;
import org.jeets.traccar.routing.TraccarRoute;
import org.jeets.traccar.routing.TraccarSetup;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
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
public class ServerManager implements BeanFactoryPostProcessor {

    /**
     * The method BeanFactoryPostProcessor.postProcessBeanFactory is called by
     * Spring startup process just after all bean definitions have been loaded, but
     * no beans have been instantiated yet.
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            setupTraccarServers(beanFactory);
        } catch (Exception e) {
            System.err.println("Context was not initialized. Traccar servers can not be launched.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Setup the Traccar servers with ports defined in the setup file and register
     * them in Spring to be handled by Camel and Netty (starter).
     * 
     * @param beanFactory for Bean registration in application context
     * @throws Exception 
     */
    private void setupTraccarServers(ConfigurableListableBeanFactory beanFactory) throws Exception {

        /* 
         * TODO: propagate configFile to ServerManager for Context.init
         * better: use property file and handling (test and prod!?)
         * the . directory refers to the project home!
         */

        // Traccar Context is mandatory (hard coded in *Protocol classes!)
        TraccarSetup.contextInit("./setup/traccar.xml");

        /*
         * The org.traccar.ServerManager scans directories or simple jars. This solution
         * doesn't work for SpringBoot jars and would require coding over several
         * ClassLoaders, paths etc. With the Reflections Library it boils down to two
         * lines at the cost of importing javassist-3.26.0-GA-sources.jar 764 kb and
         * reflections-0.9.12-sources.jar 52 kb. Scanning only takes place when starting
         * up the application, performs only once. This could be optimized by scanning
         * at Maven build time. see www.baeldung.com/reflections-library
         */
        Reflections reflections = new Reflections("org.traccar.protocol");
        Set<Class<? extends BaseProtocol>> protocolClasses = reflections.getSubTypesOf(BaseProtocol.class);
//      protocolClasses.forEach(System.out::println);

        String protocol = null;
        int port = -1;
        for (Class<? extends BaseProtocol> clazz : protocolClasses) {
//          code from BaseProtocol.nameFromClass(class);
            String className = clazz.getSimpleName(); // TeltonikaProtocol
            protocol = className.substring(0, className.length() - 8).toLowerCase();

            if (protocol.equals("teltonika")) {

                beanFactory.registerSingleton(protocol, // TeltonikaProtocol.class
                        TraccarSetup.createServerInitializerFactory(clazz));

                port = TraccarSetup.getProtocolPort(protocol);
                if (port != -1) {
                    String uri = "netty:tcp://" + host + ":" + port 
                            + "?serverInitializerFactory=#" + protocol + "&sync=" + camelNettySync;
                    beanFactory.registerSingleton(protocol + "Route", new TraccarRoute(uri, protocol));
                } else {
                    System.err.println("port# for " + protocol
                            + " is not defined in configuration file. Server is not launched!");
                }

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
