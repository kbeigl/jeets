package org.jeets.dcs;

import org.jeets.traccar.routing.TraccarRoute;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.traccar.Context;

@Configuration
// https://stackoverflow.com/questions/43272472/how-to-create-multiple-beans-of-same-type-according-to-configuration-in-spring
public class TraccarServerManager implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (int i = 0; i < 3; i++) {
            System.out.println("register my bean: " + i);
            beanFactory.registerSingleton("bean-" + i, new String("MyBean-" + i));
        }
        
//      currently this setup fails, if run stand alone
//      succeeds, if run after Parse Tests
//        which loads Context.init(file) and holds Context !
        
        String routeName = "teltonika";
        int port = -1; // 5027
        if (Context.getConfig().hasKey("teltonika.port")) {
            port = Context.getConfig().getInteger("teltonika.port");
            String uri = "netty:tcp://" + host + ":" + port + "?serverInitializerFactory=#" + routeName + "&sync=" + camelNettySync;
            beanFactory.registerSingleton("teltonikaXRoute", new TraccarRoute(uri, routeName));
        }

    }

    /**
     * The from endpoint for each protocol must be set to false!
     * <p>
     * The Traccar Pipeline and -Decoders are implemented WITH ACK response, i.e.
     * channel.writeAndFlush. Therefore the Camel endpoint, i.e. NettyConsumer,
     * should NOT return a (addtional) response. This behavior should be observed ..
     * <br>
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
