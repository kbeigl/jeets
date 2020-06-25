package org.jeets.dcs.traccar;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protobuf.Jeets;
import org.jeets.protocol.TraccarClientProtocol;
import org.jeets.protocol.TraccarProtocol;
import org.junit.Test;

public class TraccarDcsTest extends CamelTestSupport {

    @Test
	public void testDcsRoute() throws Exception {

        context.addRoutes(new RouteBuilder() {
        	public void configure() throws Exception {
//              this route segment is processed by a pool of five threads
                from("seda:jeets-dcs")
                .log("seda out: ${body}")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
//                      receives Acknowledge ?! instead of...
                        Device device = (Device) exchange.getIn().getBody();
                        System.out.println("received jpa.Device " + device.getUniqueid()
                            + " with " + device.getPositions().size() + " positions.");
                    }
                })
                .to("mock:result");
            }
        });

        MockEndpoint mock = getMockEndpoint("mock:result");
        Jeets.Acknowledge response = (Jeets.Acknowledge) template
                .requestBody("netty:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", createProtoDevice());
        System.out.println("client received response: " + response);
        assertEquals(789, response.getDeviceid());
        
        mock.expectedMessageCount(1);
        assertMockEndpointsSatisfied();
	}

//  TODO: replace with ..Samples
    Jeets.Device createProtoDevice() {
        ProtoBean protoMaker = new ProtoBean();
        Jeets.Device deviceProto = protoMaker.createProtoDevice();
        return deviceProto;
    }

	@Override
    protected Registry createCamelRegistry() throws Exception {
	    Registry registry = new SimpleRegistry();
        registry.bind("protobuffer", new TraccarProtocol(null));  // request to server
        registry.bind("ack", new TraccarClientProtocol(null));   // response to client
        return registry;
    }

    @Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new TraccarRoute();
	}

    protected CamelContext createCamelContext() throws Exception {  
        CamelContext context = super.createCamelContext();  
        context.setLoadTypeConverters(true);
        return context;  
    }  
    
}
