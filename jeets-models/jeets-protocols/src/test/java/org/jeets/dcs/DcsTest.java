package org.jeets.dcs;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.dcx.ClientAckProtoExtractor;
import org.jeets.dcx.DcsRoute;
import org.jeets.dcx.ProtoBean;
import org.jeets.dcx.steps.DeviceProtoExtractor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protobuf.Traccar;
import org.junit.Test;

public class DcsTest extends CamelTestSupport {

    @Test
	public void testDcsRoute() throws Exception {

        context.addRoutes(new RouteBuilder() {
        	public void configure() throws Exception {
//              this route segment is processed by a pool of five threads
                from("seda:jeets-dcs")
                .log("seda out: ${body}")
//                .process(new Processor() {
//                    public void process(Exchange exchange) throws Exception {
////                      receives Acknowledge ?! instead of...
//                        Device device = (Device) exchange.getIn().getBody();
//                        System.out.println("persist jpa.Device " + device.getUniqueid()
//                            + " with " + device.getPositions().size() + " positions.");
//                    }
//                })
                .to("mock:result");
            }
        });

        MockEndpoint mock = getMockEndpoint("mock:result");
        Traccar.Acknowledge response = (Traccar.Acknowledge) template
                .requestBody("netty:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", createProtoDevice());
        System.out.println("client received response: " + response);
        assertEquals(789, response.getDeviceid());
        
        mock.expectedMessageCount(1);
        assertMockEndpointsSatisfied();
	}

//  TODO: replace with ..Samples
    Traccar.Device createProtoDevice() {
        ProtoBean protoMaker = new ProtoBean();
        Traccar.Device deviceProto = protoMaker.createProtoDevice();
        return deviceProto;
    }

	@Override
    protected Registry createCamelRegistry() throws Exception {
	    Registry registry = new SimpleRegistry();
        registry.bind("protobuffer", new DeviceProtoExtractor(null));    // request to server
        registry.bind("ack", new ClientAckProtoExtractor(null));        // response to client
        return registry;
    }

    @Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new DcsRoute();
	}

    protected CamelContext createCamelContext() throws Exception {  
        CamelContext context = super.createCamelContext();  
        context.setLoadTypeConverters(true);
        return context;  
    }  
    
}
