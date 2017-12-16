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
package org.jeets.manager;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.jeets.etl.steps.DeviceWrapper;
import org.jeets.model.traccar.jpa.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedRoute extends SpringRouteBuilder {

    public void configure() throws Exception {
//      @formatter:off
//      getContext().setTracing(true);

//      etl approach to get hold of the endpoints entitymanager
        from("direct:device.in")
        .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                Device device = (Device) exchange.getIn().getBody();
                System.out.println("Device from 'direct:device.in'\n" + device);
                DeviceWrapper netDevice = new DeviceWrapper(device);
                exchange.getOut().setBody(netDevice);
            }
        })
        .to("jpa:org.jeets.model.traccar.jpa.Device?usePersist=true");
//        .to("direct:managed.end");

//      @formatter:on
    }

}
