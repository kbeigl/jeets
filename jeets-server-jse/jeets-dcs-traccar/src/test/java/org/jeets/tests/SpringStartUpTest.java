package org.jeets.tests;
/**
 * Copyright 2019 The Java EE Tracking System - JeeTS
 * Copyright 2019 Kristof Beiglböck kbeigl@jeets.org
 *
 * The JeeTS Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;
import org.apache.camel.spring.Main;
import org.traccar.Context;

public class SpringStartUpTest extends TestCase {

    public void testContextInit() throws Exception {
    	// start spring application to check if it boots OK
        Main.main("-duration", "3s");

        assertNotNull("Config was not loaded", Context.getConfig());
//      validate if default values are overridden
//      check if ports are available and other required props

        assertNotNull("DeviceManager was not loaded", Context.getDeviceManager());
        assertNotNull("IdentityManager was not loaded", Context.getIdentityManager());

        assertNotNull("MediaManager was not loaded", Context.getMediaManager());
        System.out.println(Context.getConfig().getString("media.path"));

        assertNotNull("ConnectionManager was not loaded", Context.getConnectionManager());
//      ServerManager should not be started (in addition to camel-netty)
        assertNull("ServerManager should NOT be loaded", Context.getServerManager());
        
//      CURRENTLY XML STOPS AFTER ... Context initialized from XML route!
//      ADD FULL ROUTE (step by step) and keep adding tests
//      ... and raise duration (only) if required
    }

}
