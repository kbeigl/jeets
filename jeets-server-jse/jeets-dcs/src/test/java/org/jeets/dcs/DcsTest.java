package org.jeets.dcs;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.dcs.steps.DeviceProtoExtractor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.protocol.Traccar;
import org.junit.Test;

public class DcsTest extends CamelTestSupport {

    @Test
	public void testDcsRoute() throws Exception {

        context.addRoutes(new RouteBuilder() {

        	public void configure() throws Exception {
//              this route segment is processed by a pool of five threads
                from("seda:jeets-dcs")
//              .log("seda out: ${body}")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        
//                      receives Acknowledge ?! instead of...
                        Device device = (Device) exchange.getIn().getBody();
                        System.out.println("simulate 5s persist of jpa.Device {} with {} positions." + 
                                device.getUniqueid() + device.getPositions().size());
                        
//                      ShutdownTask starts with a timeout of 10 seconds!
                        Thread.sleep(5000);
                        System.out.println("... continue after 5 seconds ...");
//                        exchange.getOut().setBody("successfully ended process");
                   }
                })
                .to("mock:result");
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:result");

//      start the test
        Traccar.Acknowledge response = (Traccar.Acknowledge) template
//        		see DcsRoute for {{dcs.props}}
//              .requestBody("netty:tcp://localhost:{{port}}?clientInitializerFactory=#ack&sync=true", createProtoDevice());
                .requestBody("netty:tcp://localhost:5200?clientInitializerFactory=#ack&sync=true", createProtoDevice());
        System.out.println("client received response: " + response);
        assertEquals(789, response.getDeviceid());
        
        mock.expectedMessageCount(1);
//        mock.expectedMinimumMessageCount(1);
//        List<Exchange> messages = mock.getExchanges();
        assertMockEndpointsSatisfied();
//        printExchangeList(messages);
	}

//  TODO: replace with ..Samples
    Traccar.Device createProtoDevice() {
        ProtoBean protoMaker = new ProtoBean();
        Traccar.Device deviceProto = protoMaker.createProtoDevice();
        return deviceProto;
    }

	@Override
	protected JndiRegistry createRegistry() throws Exception {
	    JndiRegistry registry = super.createRegistry();

//      registry.bind("{{dcs.protobuffer.protocol}}", new DeviceProtoExtractor(null));    // request  to server
        registry.bind("protobuffer", new DeviceProtoExtractor(null));    // request  to server
        registry.bind("ack", new ClientAckProtoExtractor(null));    // response to client

        return registry;
    }

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new DcsRoute();
	}
	
    protected CamelContext createCamelContext() throws Exception {  
        CamelContext context = super.createCamelContext();  

//      props belong to CamelContext, set directly after creation
        PropertiesComponent props = (PropertiesComponent) context.getPropertiesComponent();  
        props.setLocation("classpath:dcs.properties");
//      TODO: validate registering as "properties", advantages?
//      context.addComponent("properties", props);
  
        return context;  
    }  
    
}
