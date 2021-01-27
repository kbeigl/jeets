/**
 * Copyright 2017 The Java EE Tracking System - JeeTS Copyright 2017 Kristof Beiglböck
 * kbeigl@jeets.org
 *
 * <p>The JeeTS Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jeets.etl;

/** Command line tool to run the JeeTS-ETL module */
public class Main extends org.apache.camel.spring.Main {
  public static void main(String... args) throws Exception {
    new Main().run(args);
  }
}
