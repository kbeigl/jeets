/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jeets.dcs;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/* test the Component without netty4 source */
public class DcsComponentTest extends CamelTestSupport {

//    @Override
//    protected RouteBuilder createRouteBuilder() throws Exception {
//        return new RouteBuilder() {
//            public void configure() {
//            }
//        };
//    }

    @Test
    public void testFromOneToAnotherDcs() throws Exception {
        context().addRoutes(new RouteBuilder() {
            public void configure() {
                from("jeets-dcs://from")    // instance one
                .to("jeets-dcs://to")       // instance two
                .to("mock:extselfref");
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:extselfref");
        mock.expectedMinimumMessageCount(3);
        List<Exchange> messages = mock.getExchanges();
        assertMockEndpointsSatisfied();
        printExchangeList(messages);
    }    

    @Test
    public void testFromToDcs() throws Exception {
        context().addRoutes(new RouteBuilder() {
            public void configure() {
                from("jeets-dcs://device")  // instance two - DcsConsumer
                .to("jeets-dcs://device")   // instance two - DcsProducer
                .to("mock:selfref");
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:selfref");
        mock.expectedMinimumMessageCount(3);
        List<Exchange> messages = mock.getExchanges();
        assertMockEndpointsSatisfied();
        printExchangeList(messages);
    }    

    /**
     * Test jeets-dcs at start of route. <br>
     * Note that this test only creates the Consumer!
     * <p>
     * If you put the Component at the start of a route then there must be an
     * implementation of the Consumer part of the Component. This does the work
     * of converting or creating the specific input/request into a Camel
     * Exchange - that can travel down a Route.
     * <p>
     * Note that the final hard coded source Endpoint from("jeets-dcs://device")
     * is actually not the beginning of a Route, since jeets-dcs feeds it
     * with the Route from("netty4").to("jeets-dcs://device") !
     * <p>
     * Currently the poll creates a new Exchange two times per second.
     * An improved version should through an Exception from EP.createConsumer method (?).
     *  
     */
    @Test
    public void testFromDcsOnly() throws Exception {
        context().addRoutes(new RouteBuilder() {
            public void configure() {
//              fire twice per second
                from("jeets-dcs://one").to("mock:one");
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:one");
        mock.expectedMinimumMessageCount(3);
        List<Exchange> messages = mock.getExchanges();
        assertMockEndpointsSatisfied();
        printExchangeList(messages);
    }
    
    public static void printExchangeList(List<Exchange> messages) {
        System.out.println(messages.size() + " Exchanges stored");
        for (int exChgNr = 0; exChgNr < messages.size(); exChgNr++) {
            Exchange exChg = messages.get(exChgNr); 
            System.out.println( exChgNr + ": In  message: " + exChg.getIn().getBody());
            System.out.println( exChgNr + ": Out message: " + exChg.getOut().getBody());
//          System.out.println("Out message: " + exChg.getOut(jpa.Device.class)); // validate type
        }
    }

}
