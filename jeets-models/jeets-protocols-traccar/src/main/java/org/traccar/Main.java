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

import java.util.Timer;
import java.util.TimerTask;
import java.util.Locale;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final long CLEAN_PERIOD = 24 * 60 * 60 * 1000; // 24 hours

    private Main() {
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
//          logSystemInfo();
//          LOGGER.info("Version: " + Context.getAppVersion());
            LOGGER.info("Starting Device Communication Servers ...");

//          see Context callerClassName
            Context.getServerManager().start();

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                        LOGGER.warn("Clear history skipped");
                }
            }, 0, CLEAN_PERIOD);

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
