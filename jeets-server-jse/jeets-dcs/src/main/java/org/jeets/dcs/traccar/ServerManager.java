package org.jeets.dcs.traccar;

import org.jeets.traccar.routing.TraccarRoute;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.traccar.Context;

/**
 * This class is modeled after and replaces Traccar's ServerManager only with
 * Spring and camel-netty server management via composing URI Strings.
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

//      GenericBeanDefinition bd = new GenericBeanDefinition();
//      bd.setBeanClass(MyBean.class);
//      bd.getPropertyValues().add("strProp", "my string property");
//      ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("myBeanName", bd);
//      registerBeanDefinition vs registerSingleton ??

//      for (int i = 0; i < 3; i++) {
        
//      currently this setup 
//         fails, if run stand alone
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
