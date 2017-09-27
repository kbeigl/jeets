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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.jeets.etl.steps.NetworkDevice;
import org.jeets.model.traccar.jpa.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtlRoute extends SpringRouteBuilder {

//  use Camel .log instead
    private static final Logger LOG = LoggerFactory.getLogger(EtlRoute.class);
    // "etl.device.port"
    static final int PORT = Integer.parseInt(System.getProperty("port", "5200"));

    public void configure() throws Exception {
        LOG.info("configure EtlRoutes .. ");
//      @formatter:off
        from("seda:jeets-dcs")

        .log("${body}")
        .enrich("direct:geocode")
        .log("${body}")
        .log("${header.address}")
        
//      send a shallow copy (and geocode might be added later!)
//      .multicast().parallelProcessing().to("jpa:y", "mq:z")
        
//      .convertBodyTo(Device.class)
        .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
                Device device = (Device) exchange.getIn().getBody();
                NetworkDevice netDevice = new NetworkDevice(device);
                exchange.getOut().setBody(netDevice);
            }
        })
        .to("jpa:org.jeets.model.traccar.jpa.Device?usePersist=true");

        /* content enrichment with Camel Google Geocoder
         *  input:
         * output:
         */
        from("direct:geocode")
        .to("geocoder:latlng:40.714224,-73.961452")
        .log("Location ${header.CamelGeocoderAddress} "
                + "is at lat/lng: ${header.CamelGeocoderLatlng} and "
                + "in country ${header.CamelGeoCoderCountryShort}");
//      @formatter:on
    }

}
