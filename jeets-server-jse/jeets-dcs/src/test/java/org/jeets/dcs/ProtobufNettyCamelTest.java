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
package org.jeets.dcs;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.jeets.dcs.ProtoBean;
import org.jeets.dcs.steps.DeviceProtoExtractor;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.Traccar.Acknowledge;

/**
 * Testing Protobuffers from Client to Server and back.
 * 
 * @author kbeigl@jeets.org
 */
public class ProtobufNettyCamelTest extends CamelTestSupport {
//  TODO: analyze with extends org.apache.camel.component.netty4.BaseNettyTest;
//        includes expression resolving: localhost:{{port}}

//  private static final Logger LOG = LoggerFactory.getLogger(ProtobufNettyCamelTest.class);

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("ack", new ClientAckProtoExtractor(null));    // client
        registry.bind("device", new DeviceProtoExtractor(null));    // server
        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("netty4:tcp://localhost:5200?serverInitializerFactory=#device&sync=true")
//                from("netty4:tcp://localhost:{{port}}?serverInitializerFactory=#device&sync=true")
                .log("proto: ${body}")
                .to("jeets-dcs://device");

                from("jeets-dcs://device")  // Endpoint of DCS with jpa.Device Entity
//                .log("BODY: ${body}")
                .to("mock:result");         // Endpoint Consumer Component (ETL etc.)
            }
        };
    }

    @Test
//  does not involve Netty!
    public void mockDcsPipelines() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        Acknowledge response = (Acknowledge) 
                template.requestBody("jeets-dcs://device", createProtoDevice());
        mock.expectedBodiesReceived("DcsConsumer String Body");
//      DcsConsumer.poll for every MessageCount!
        mock.expectedMessageCount(3);
        assertMockEndpointsSatisfied();
        List<Exchange> messages = mock.getExchanges();
        DcsComponentTest.printExchangeList(messages);
}

    @Test
//  does not involve Netty!
    public void testDcsPipelines() throws Exception {
        Acknowledge response = (Acknowledge) 
                template.requestBody("jeets-dcs://device", createProtoDevice());
        assertNotNull(response);
    }

    @Test
    public void testNettyProtbufferPipelines() throws Exception {
        Traccar.Acknowledge response = (Traccar.Acknowledge) template
//              .requestBody("netty4:tcp://localhost:{{port}}?clientInitializerFactory=#ack&sync=true", createProtoDevice());
                .requestBody("netty4:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", createProtoDevice());
        assertEquals(789, response.getDeviceid());
    }
    
/*
    //  simulate client
        ProtoBean protoMaker = new ProtoBean();
        Traccar.Device deviceProto = protoMaker.createProtoDevice();
        System.out.println("client sends: " + deviceProto.toString());
        ProducerTemplate template = context.createProducerTemplate();
    //    template.sendBody("jeets-dcs://device", deviceProto);
    //  without protobuffer channel pipleline !! ???
        Acknowledge response = (Acknowledge) template.requestBody("jeets-dcs://device", deviceProto);
        System.out.println("received: " + response.toString());
        Thread.sleep(1000);
        context.stop();
 */

    private Traccar.Device createProtoDevice() {
        ProtoBean protoMaker = new ProtoBean();
        Traccar.Device deviceProto = protoMaker.createProtoDevice();
        return deviceProto;
    }

}
