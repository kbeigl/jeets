/*
 * Copyright 2015 - 2019 Anton Tananaev (anton@traccar.org)
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
import org.traccar.config.Config;
//import org.traccar.database.CommandsManager;
import org.traccar.database.ConnectionManager;
import org.traccar.database.DeviceManager;
import org.traccar.database.IdentityManager;
import org.traccar.database.MediaManager;
import org.traccar.helper.Log;

/**
 * This Context is derived from Traccar. On a longer term it might be replaced
 * with some specified Context, i.e. Camel-, Spring-, JNDI- etc.), *Managers
 * might be registered.
 *
 * @author kbeigl@jeets.org
 */
public final class Context {

    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private Context() {
    }

    private static Config config;

    public static Config getConfig() {
        return config;
    }

    private static IdentityManager identityManager;

    public static IdentityManager getIdentityManager() {
        return identityManager;
    }

    private static MediaManager mediaManager;

    public static MediaManager getMediaManager() {
        return mediaManager;
    }

    private static DeviceManager deviceManager;

    public static DeviceManager getDeviceManager() {
        return deviceManager;
    }

    private static ConnectionManager connectionManager;

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    private static ServerManager serverManager;

    public static ServerManager getServerManager() {
        return serverManager;
    }

    /**
     * Distinguish original Traccar source, i.e. legacy, from Camel Spring approach.
     * Should be initialized explicitly to avoid unwanted default behavior.
     */
    static boolean legacy;

//    private static CommandsManager commandsManager;
//    public static CommandsManager getCommandsManager() {
//        return commandsManager;
//    }

    public static void init(String configFile) throws Exception {
//      fork for camel <-> traccar.Main (temporary for regression tests)
        String callerClassName = new Exception().getStackTrace()[1].getClassName();
        legacy = callerClassName.equals("org.traccar.Main");

        try {
            config = new Config(configFile);
        } catch (Exception e) {
            config = new Config();
            Log.setupDefaultLogger();
            throw e;
        }
//      setup Logger analog to traccar in Camel ...
        if ((legacy) && (config.getBoolean("logger.enable"))) {
            Log.setupLogger(config);
        }

//      LOGGER.warn("DataManager was removed -> remove database.url");

        mediaManager = new MediaManager(config.getString("media.path"));
//      deviceManager = new DeviceManager( dataManager );
        deviceManager = new DeviceManager();
        identityManager = deviceManager;
        connectionManager = new ConnectionManager();

        if (config.getBoolean("sms.enable")) {
            LOGGER.warn("SMS Manager was removed in ETL version");
        }
        if (config.getBoolean("event.enable")) {
            LOGGER.warn("initEventsModule is skipped - unclear operation");
//          initEventsModule();
//          check Camel and/or Netty alternatives (Notifier.java?)
        }
        if (legacy) {
            serverManager = new ServerManager();
        }
    }

    /* required for protocol testing
     * This causes a conflict when running Camel tests.
     * When the ClassFinder loads
     * *Test.class > BaseTest > Context.init(new TestIdentityManager())
     * which overrides the Context (see below).
     * Camel tests sets up Context.init( traccar.xml ) from the real file.
     * Remodel provided tests to real Context incl IdentityManager?
     */
    public static void init(IdentityManager testIdentityManager, MediaManager testMediaManager) {
        config = new Config();
        identityManager = testIdentityManager;
    }

}
