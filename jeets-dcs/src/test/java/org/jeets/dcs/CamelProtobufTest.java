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

import org.apache.camel.CamelException;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.netty4.BaseNettyTest;
// org.apache.camel.model.dataformat.ProtobufDataFormat;   ??
import org.apache.camel.dataformat.protobuf.ProtobufDataFormat;
import org.junit.Test;
import org.jeets.dcs.ProtoBean;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.Traccar.Device;

/**
 * Testing different ways to un/marshal with Camel Route
 * 
 * @author kbeigl@jeets.org
 */
public class CamelProtobufTest extends BaseNettyTest {
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
//              un/marshal Protobuffers (does not involve netty!)
//              You can store your protobuf data in a BytesMessage and transmit it that way 
//              to allow you to marshal and unmarshal the data on either end.
//              www.programcreek.com/java-api-examples/index.php?api=org.apache.camel.builder.RouteBuilder
                ProtobufDataFormat format = new ProtobufDataFormat(Device.getDefaultInstance());

                from("direct:in").marshal(format);
                from("direct:marshal").marshal().protobuf();
                
                from("direct:back").unmarshal(format)
                .to("mock:reverse");

                from("direct:unmarshalA").unmarshal()
                .protobuf("org.jeets.protocol.Traccar$Device")
                .to("mock:reverse");

                from("direct:unmarshalB").unmarshal()
                .protobuf(Traccar.Device.getDefaultInstance())
                .to("mock:reverse");
            }
        };

    }

    @Test
    public void testMarshalAndUnmarshalWithDataFormat() throws Exception {
        marshalAndUnmarshal("direct:in", "direct:back");
    }
    
    @Test
    public void testMarshalAndUnmarshalWithDSL1() throws Exception {
        marshalAndUnmarshal("direct:marshal", "direct:unmarshalA");
    }
    
    @Test
    public void testMarshalAndUnmarshalWithDSL2() throws Exception {
        marshalAndUnmarshal("direct:marshal", "direct:unmarshalB");
    }
    
    @Test
    public void testMarshalAndUnmarshalWithDSL3() throws Exception {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:unmarshalC").unmarshal().protobuf(new CamelException("wrong instance"))
                        .to("mock:reverse");
                }
            });
            fail("Expect the exception here");
        } catch (Exception ex) {
            assertTrue("Expect FailedToCreateRouteException", ex instanceof FailedToCreateRouteException);
            assertTrue("Get a wrong reason", ex.getCause() instanceof IllegalArgumentException);
        }
    }

    private void marshalAndUnmarshal(String inURI, String outURI) throws Exception {
        
        ProtoBean protoMaker = new ProtoBean();
        Traccar.Device input = protoMaker.createProtoDevice();

        MockEndpoint mock = getMockEndpoint("mock:reverse");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(Traccar.Device.class);
        mock.message(0).body().isEqualTo(input);

        Object marshalled = template.requestBody(inURI, input);     // from
        template.sendBody(outURI, marshalled);                      // to

        mock.assertIsSatisfied();

        Traccar.Device output = mock.getReceivedExchanges().get(0).getIn().getBody(Traccar.Device.class);
        assertEquals("11", output.getUniqueid());
    }

}
