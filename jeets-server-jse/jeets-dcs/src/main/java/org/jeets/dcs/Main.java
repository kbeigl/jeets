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

        if (args.length <= 0) {
            throw new RuntimeException("Configuration file is not provided");
        }
//      TODO: propagate configFile to ServerManager for Context.init
//      better: use property file and handling (test and prod!?)
        final String configFile = args[args.length - 1];

        logSystemInfo();

        SpringApplication.run(Main.class, args);
        System.out.println("SpringApplication running ...");

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
