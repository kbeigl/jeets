/**
 * Copyright 2017 The Java EE Tracking System - JeeTS
 * Copyright 2017 Kristof Beiglböck kbeigl@jeets.org
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
package org.jeets.etl;

import junit.framework.TestCase;
import org.apache.camel.spring.Main;

public class IntegrationTest extends TestCase {
//  Currently this test is only starting the application to check if it boots OK
//  A client could be started in the testing environment
//  but first the PG DB has to be substituted by an in memory DB (see PU Testing)
    public void testEtlRoutes() throws Exception {
//      boot up Spring application context for 5 seconds to check that it works OK
        Main.main("-duration", "3s");
    }
}
