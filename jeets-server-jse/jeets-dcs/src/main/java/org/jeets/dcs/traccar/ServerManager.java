package org.jeets.dcs.traccar;

import org.jeets.traccar.routing.TraccarRoute;
import org.jeets.traccar.routing.TraccarSetup;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

/**
 * This class is modeled after and replaces Traccar's ServerManager only with
 * Spring and camel-netty server management by composing URI Strings.
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

//      Traccar Context is mandatory!
//      TODO: propagate configFile to ServerManager for Context.init
//      better: use property file and handling (test and prod!?)
//      the . directory refers to the project home! setup dir has to exist.
        TraccarSetup.contextInit("./setup/traccar.xml");

//      define internal sub/method/s
//      for (int i = 0; i < 3; i++) {

        String protocol = "teltonika";
        int port = -1; // 5027
        try {
            port = TraccarSetup.getProtocolPort(protocol);
        } catch (Exception e) {
            System.err.println(e.getMessage());
//          e.printStackTrace();
//          Context not initialized > System.exit
//          throw new RuntimeException(e.getMessage()); 
        }
//      catch port = 0 / -1 ?
//      else
//      java.lang.IllegalArgumentException: hostname can't be null
//      at java.net.InetSocketAddress.checkHost(InetSocketAddress.java:149)

        String uri = "netty:tcp://" + host + ":" + port 
                + "?serverInitializerFactory=#" + protocol + "&sync=" + camelNettySync;
        beanFactory.registerSingleton("teltonikaXRoute", new TraccarRoute(uri, protocol));

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
