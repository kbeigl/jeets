/*
 * Copyright 2012 - 2018 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.Charset;
import java.util.Locale;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Main() {
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

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.ENGLISH);

        if (args.length <= 0) {
            throw new RuntimeException("Configuration file is not provided");
        }

        final String configFile = args[args.length - 1];

        run(configFile);
    }

    public static void run(String configFile) {
        try {
            Context.init(configFile);
            logSystemInfo();
            LOGGER.info("Starting Device Communication Servers ...");

//          see Context callerClassName
            Context.getServerManager().start();

            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    LOGGER.error("Thread exception", e);
                }
            });

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    LOGGER.info("Shutting down servers ...");
                    Context.getServerManager().stop();
                    LOGGER.info(" ... done");   // never reached ?
                }
            });
        } catch (Exception e) {
            LOGGER.error("Main method error", e);
            throw new RuntimeException(e);
        }
    }

}
