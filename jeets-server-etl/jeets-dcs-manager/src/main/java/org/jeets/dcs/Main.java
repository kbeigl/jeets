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

@SpringBootApplication
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Locale.setDefault(Locale.ENGLISH);

        logSystemInfo();

        SpringApplication.run(Main.class, args);
/*      BindException/s can occur when Camel starts - how to handle and report at startup?
        INFO  o.a.c.i.engine.AbstractCamelContext  - Apache Camel 3.3.0 (CamelContext: camel-1) is starting
        INFO  o.a.c.component.netty.NettyComponent - Creating shared NettyConsumerExecutorGroup with 9 threads
        INFO  o.a.c.c.n.SingleTCPNettyServerBootstrapFactory - ServerBootstrap   binding to 0.0.0.0:5200
        INFO  o.a.c.c.n.SingleTCPNettyServerBootstrapFactory - ServerBootstrap UNBINDING 0.0.0:5200
        INFO  o.a.c.component.netty.NettyConsumer - Netty consumer UNBOUND from: 0.0.0.0:5200
        ERROR o.a.c.i.engine.AbstractCamelContext - Error starting CamelContext (camel-1) due to exception thrown: 
                java.net.BindException: Address already in use: bind        System.out.println("SpringApplication running ...");
                
        don't stop startup, if some servers fail
        old code from DcsRoutesFactory:
        Map<String, Integer> portFailures = new ConcurrentHashMap<>();
        try {
            camelContext.addRoutes(new DcsRouteBuilder(camelContext, uri, serverInitializerName));
        } catch (Exception e) { // instanceof java.net.BindException
            portFailures.put(uri, server.getPort());
        }
        if (!portFailures.isEmpty()) {
            System.err.println("The following routes could not created due to 'port already in use'");
            portFailures.forEach((k, v) -> {
                System.err.println("\tport=" + v + " - uri=" + k);
            });
        }

 */
//      TODO realize these via Spring life cycle management
//      Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//      Runtime.getRuntime().addShutdownHook(new Thread() { ..
//      docs.spring.io/spring-boot/docs/2.1.10.RELEASE/reference/html/boot-features-spring-application.html
//      ctx.registerShutdownHook();
        
    }

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
