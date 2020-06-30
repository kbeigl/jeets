package org.jeets.dcs.traccar;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.protobuf.Jeets;
import org.jeets.protocol.JeetsClientProtocol;
import org.jeets.protocol.JeetsProtocol;
import org.jeets.protocol.util.Samples;
import org.jeets.traccar.routing.TraccarRoute;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraccarDcsTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(TraccarDcsTest.class);

    @Test
	public void testTraccarDcsRoute() throws Exception {

        context.addRoutes(new RouteBuilder() {
        	public void configure() throws Exception {
                from("direct:traccar.model")
                .log("DCS out traccar.model: ${body}")
                .to("mock:result");
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:result");

        Jeets.Acknowledge response = (Jeets.Acknowledge) template
                .requestBody("netty:tcp://localhost:5200"
                        + "?clientInitializerFactory=#ack&sync=true",
                        Samples.createDeviceWithPositionWithOneEvent().build());
//                      Samples.createDeviceWithPositionWithTwoEvents().build());
        LOG.info("client received response: " + response);
        assertEquals(789, response.getDeviceid());
        
        mock.expectedMessageCount(1);
        assertMockEndpointsSatisfied();
	}

    @Override
	protected RouteBuilder createRouteBuilder() throws Exception {
        String routeId = "jeets"; // i.e Traccar JeeTS Protocol
        String uri = "netty:tcp://" + host + ":" + port 
                + "?serverInitializerFactory=#" + routeId + "&sync=" + camelNettySync;
        return new TraccarRoute(uri, routeId);
	}

    @Override
    protected Registry createCamelRegistry() throws Exception {
        Registry registry = new SimpleRegistry();
        registry.bind("jeets", new JeetsProtocol(null));      // request to server
        registry.bind("ack", new JeetsClientProtocol(null)); // response to client
        return registry;
    }

    protected CamelContext createCamelContext() throws Exception {  
        CamelContext context = super.createCamelContext();  
        context.setLoadTypeConverters(true);
        return context;  
    }
    
    private boolean camelNettySync = false;
    private String host = "localhost";  //"0.0.0.0";
    private int port = 5200;
}
