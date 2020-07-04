package org.jeets.dcs;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.camel.component.netty.ServerInitializerFactory;
import org.jeets.traccar.routing.TraccarSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.traccar.Context;
import org.traccar.protocol.TeltonikaProtocol;

import io.netty.handler.codec.string.StringDecoder;

@SpringBootApplication  // → @SpringBootConfiguration → @Configuration
// convenience annotation equivalent to declaring @Configuration, @EnableAutoConfiguration, @ComponentScan.
@ComponentScan(basePackages="org.jeets.dcs")
// By default, the @SpringBootApplication annotation scans all classes in the same package (in other project!?) or below.
public class Main {

//  private Main() {};

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Locale.setDefault(Locale.ENGLISH);

        if (args.length <= 0) {
            throw new RuntimeException("Configuration file is not provided");
        }

        final String configFile = args[args.length - 1];
        
        System.out.println("running Main ...");

        try {
//          Traccar context
//            Context.init(configFile); temporarily handled by TraccarSetup
//          TODO fix logger
            logSystemInfo();
            
            /*
             * You should use an ApplicationContext with GenericApplicationContext and its
             * subclass AnnotationConfigApplicationContext as the common implementations for
             * custom bootstrapping. These are the primary entry points to Spring’s core
             * container for all common purposes: loading of configuration files, triggering
             * a classpath scan, programmatically registering bean definitions and annotated
             * classes, and (as of 5.0) registering functional bean definitions.
             */
//          Spring(BootApplication) context
//          ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);

//          method #1 works with new Object() and lambda but not with method below
//          ctx.registerBean("teltonika", ServerInitializerFactory.class, () -> 
//              TraccarSetup.createServerInitializerFactory(TeltonikaProtocol.class));
  
//            ctx.registerBean("stringDecoder", StringDecoder.class,
//                    () -> new StringDecoder());
  
//          method #2 with GenericBeanDefinition
//            GenericBeanDefinition gbd = new GenericBeanDefinition();
//            gbd.setBeanClass(ServerInitializerFactory.class);
//            ctx.registerBeanDefinition("teltonika", beanDefinition);
            
//          check not null (not sufficient!)
            ServerInitializerFactory sif = (ServerInitializerFactory) ctx.getBean("teltonika");
            System.out.println("HASHCODE" + sif.hashCode());
            
//          can't call post processed beans here yet
//            String myBean = (String) ctx.getBean("bean-1");
//            System.out.println(myBean.toString());
//            ctx.refresh();
            
//          TODO realize these via Spring life cycle management, see deprecated Camel below
//          Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//          Runtime.getRuntime().addShutdownHook(new Thread() { ..
//          ctx.registerShutdownHook();
            
            SpringApplication.run(Main.class, args);

            System.out.println("SpringApplication running ...");

        } catch (Exception e) {
            System.err.println("Main method error: " + e);
            throw new RuntimeException(e);
        }
    }

    /* create this Camel mechanism (doesn't work here) in Spring
     * see https://camel.apache.org/components/3.0.x/spring-boot.html
     * CUSTOM CAMEL CONTEXT CONFIGURATION 
     * beforeApplicationStart(CamelContext context)
    public static class Events extends MainListenerSupport {
        public void afterStart(MainSupport main) { }
        public void beforeStop(MainSupport main) { }
    }    */

    public static void logSystemInfo() {
        try {
            OperatingSystemMXBean operatingSystemBean = ManagementFactory.getOperatingSystemMXBean();
            LOGGER.info("Operating system"
                    + " name: " + operatingSystemBean.getName()
                    + " version: " + operatingSystemBean.getVersion()
                    + " architecture: " + operatingSystemBean.getArch());

            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            LOGGER.info("Java runtime"
                    + " name: " + runtimeBean.getVmName()
                    + " vendor: " + runtimeBean.getVmVendor()
                    + " version: " + runtimeBean.getVmVersion());

            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            LOGGER.info("Memory limit"
                    + " heap: " + memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024) + "mb"
                    + " non-heap: " + memoryBean.getNonHeapMemoryUsage().getMax() / (1024 * 1024) + "mb");

            LOGGER.info("Character encoding: "
                    + System.getProperty("file.encoding") + " charset: " + Charset.defaultCharset());

        } catch (Exception error) {
            LOGGER.warn("Failed to get system info");
        }
    }

}
