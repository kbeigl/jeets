package org.jeets.dcs;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication  // → @SpringBootConfiguration → @Configuration
// convenience annotation equivalent to declaring @Configuration, @EnableAutoConfiguration, @ComponentScan.
@ComponentScan(basePackages="org.jeets.dcs")
// By default, the @SpringBootApplication annotation scans all classes in the same package (in other project!?) including sub packages.
public class Main {

//  private Main() {};

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Locale.setDefault(Locale.ENGLISH);

//      TODO: propagate path and file to ServerManager for Context.init(configFile)
//      better: use property file and handling
        if (args.length <= 0) {
            throw new RuntimeException("Configuration file is not provided");
        }
        final String configFile = args[args.length - 1];

        logSystemInfo();
            
        /*
         * You should use an ApplicationContext with GenericApplicationContext and its
         * subclass AnnotationConfigApplicationContext as the common implementations for
         * custom bootstrapping. These are the primary entry points to Spring’s core
         * container for all common purposes: loading of configuration files, triggering
         * a classpath scan, programmatically registering bean definitions and annotated
         * classes, and (as of 5.0) registering functional bean definitions.
         */
//      Spring(BootApplication) context
//      ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
//      AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
//      String[] beanNames = ctx.getBeanDefinitionNames();
//      Arrays.sort(beanNames);
//      for (String beanName : beanNames) { System.out.println(beanName); }
//      ServerInitializerFactory sif = (ServerInitializerFactory) ctx.getBean("teltonika");
//      System.out.println("HASHCODE" + sif.hashCode());

//      TODO realize these via Spring life cycle management, see deprecated Camel below
//      Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//      Runtime.getRuntime().addShutdownHook(new Thread() { ..
//      docs.spring.io/spring-boot/docs/2.1.10.RELEASE/reference/html/boot-features-spring-application.html
//      ctx.registerShutdownHook();
        
//      can't call post processed beans before starting SpringApp

        SpringApplication.run(Main.class, args);
        System.out.println("SpringApplication running ...");

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
