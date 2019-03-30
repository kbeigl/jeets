package org.jeets.util;

import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

public final class MultiRegistry {

    private MultiRegistry() {
    }

    /**
     * This method is from github.com/yuruki/camel-runner
     * (com.github.yuruki.camel.runner.CamelRunnerMain). It takes different
     * Registries running in different environments into account.
     * <p>
     * Has to be improved for Spring camelContext.getApplicationContextClassLoader()!
     * <p>
     * stackoverflow.com/questions/40503184/camel-how-to-add-something-to-registry-with-java-in-general
     * <br>
     * www.programcreek.com/java-api-examples/?api=org.apache.camel.spi.Registry
     */
    public static <T extends Registry> void addToRegistry(final T registry, final String name, final Object bean) {

        Registry reg = registry;
        // Unwrap PropertyPlaceholderDelegateRegistry
        if (registry instanceof PropertyPlaceholderDelegateRegistry) {
            reg = ((PropertyPlaceholderDelegateRegistry) reg).getRegistry();
        }

//      if you are using the Spring framework to define your routes,
//      the Spring ApplicationContextRegistry plug-in is automatically
//      installed in the current CamelContext instance.
//      if (reg instanceof ApplicationContextRegistry) {
////        create CompositeRegistry with ApplicationContextRegistry!?
////        ApplicationContextRegistry acRegistry = (ApplicationContextRegistry) reg;
//          DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
//          BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(bean.getClass())
//              .addPropertyValue(name, bean);
//          beanFactory.registerBeanDefinition(name, b.getBeanDefinition());
////        BeanInstantiationException:
////        Failed to instantiate [org.traccar.TrackerServer$1]: No default constructor found
////        Object myBean = beanFactory.getBean(bean.getClass());
////        skip lookup below
//          return;
//      } else
        if (reg instanceof CompositeRegistry) {
            // getRegistryList() not available in Camel 2.12
            SimpleRegistry r = new SimpleRegistry();
            r.put(name, bean);
            ((CompositeRegistry) reg).addRegistry(r);
        } else if (reg instanceof JndiRegistry) {
            ((JndiRegistry) reg).bind(name, bean);
        } else if (reg instanceof SimpleRegistry) {
            ((SimpleRegistry) reg).put(name, bean);

        } else {
            throw new IllegalArgumentException("Couldn't add bean. Unknown registry type: " + reg.getClass());
        }
        // wait for registration !?
        if (registry.lookupByName(name) != bean) {
            throw new IllegalArgumentException("Couldn't add bean. Bean not found from the registry.");
        }
    }

}
