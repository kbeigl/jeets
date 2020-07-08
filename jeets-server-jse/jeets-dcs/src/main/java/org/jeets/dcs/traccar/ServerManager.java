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
import org.traccar.protocol.TeltonikaProtocol;

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
        setupTraccarServers(beanFactory);
    }

    /**
     * Setup the Traccar servers with ports defined in the setup file and register
     * them in Spring to be handled by Camel and Netty (starter).
     * 
     * @param beanFactory
     */
    private void setupTraccarServers(ConfigurableListableBeanFactory beanFactory) {
        // Traccar Context is mandatory!
        // TODO: propagate configFile to ServerManager for Context.init
        // better: use property file and handling (test and prod!?)
        // the . directory refers to the project home! setup dir has to exist.
        TraccarSetup.contextInit("./setup/traccar.xml");

        /*
         * The org.traccar.ServerManager scans directories or simple jars. This solution
         * doesn't work for SpringBoot jars and would require coding over several
         * ClassLoaders etc. With the reflections library it boils down to a few lines
         * at the cost of importing javassist-3.26.0-GA-sources.jar 764 kb and
         * reflections-0.9.12-sources.jar 52 kb. The scanning takes place when starting
         * up the application, performs only once. This could be optimized by scanning
         * via Maven. see www.baeldung.com/reflections-library
         */
        Reflections reflections = new Reflections("org.traccar.protocol");
//      INFO Reflections took 12 sec to scan 2 urls, producing 11 keys and 754 values 
        Set<Class<? extends BaseProtocol>> set = 
                reflections.getSubTypesOf(BaseProtocol.class);
        set.forEach(System.out::println);
        
        

        // define internal sub/method/s
        // for (int i = 0; i < 3; i++) {
        
        String protocol = "teltonika"; // or "TeltonikaProtocol"
        int port = -1;

        beanFactory.registerSingleton(protocol, 
                TraccarSetup.createServerInitializerFactory(TeltonikaProtocol.class));

        try {
            port = TraccarSetup.getProtocolPort(protocol);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            // e.printStackTrace();
            // Context not initialized > System.exit
            // throw new RuntimeException(e.getMessage());
        }
        // catch port = 0 / -1 ? else:
        // java.lang.IllegalArgumentException: hostname can't be null
        // at java.net.InetSocketAddress.checkHost(InetSocketAddress.java:149)

        String uri = "netty:tcp://" + host + ":" + port 
                + "?serverInitializerFactory=#" + protocol + "&sync=" + camelNettySync;
        beanFactory.registerSingleton(protocol + "Route", new TraccarRoute(uri, protocol));
    }

//  add get/setters for host ! Individual hosts for different protocols ?

    /**
     * The Consumer Endpoint (from) for each Traccar protocol must be set to false!
     * <p>
     * The Traccar Pipeline and -Decoders are implemented WITH ACK response, i.e.
     * channel.writeAndFlush. Therefore the Camel endpoint, i.e. NettyConsumer,
     * should NOT return a (additional) response. This behavior should be observed
     * .. <br>
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
