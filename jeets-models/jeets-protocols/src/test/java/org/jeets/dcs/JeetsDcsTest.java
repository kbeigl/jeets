package org.jeets.dcs;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.protobuf.Traccar;
import org.jeets.protocol.DcsRouteJeets;
import org.jeets.protocol.TraccarClientProtocol;
import org.jeets.protocol.TraccarProtocol;
import org.jeets.protocol.util.Samples;
import org.junit.Test;
//import org.traccar.Main;

public class JeetsDcsTest extends CamelTestSupport {

    @Test
    public void testJeetsDcsRoute() throws Exception {

//      .. and pickup output: jpa.Device
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:jeets.model.traccar.jpa.Device")
                .to("mock:result");
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:result");

//      start test
        Traccar.Acknowledge response = (Traccar.Acknowledge) template
                .requestBody("netty:tcp://localhost:5200"
                        + "?clientInitializerFactory=#ack&sync=true", 
                        Samples.createDeviceWithPositionWithOneEvent());

//      evaluate and assert result/s
        System.out.print("client received response: " + response);
        assertEquals(789, response.getDeviceid());
        
        mock.expectedMessageCount(1);
        assertMockEndpointsSatisfied();
    }

//  @Test
//  public void testTraccarDcsRoute() throws Exception {
//// point to jeets-data config folder 
//// or create test.xml file with one or two ports only.
//      Main.run("./setup/traccar.xml");
//  }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
//      'traccar' must be registered before use
        String from = "netty:tcp://localhost:5200"
                + "?serverInitializerFactory=#traccar&sync=true";
        String routeId = "traccar";
        return new DcsRouteJeets(from, routeId);
    }

    @Override
    protected Registry createCamelRegistry() throws Exception {
        Registry registry = new SimpleRegistry();
        registry.bind("traccar", new TraccarProtocol(null));    // request to server
        registry.bind("ack", new TraccarClientProtocol(null)); // response to client
        return registry;
    }

    /** load typeConverters from config file */
    protected CamelContext createCamelContext() throws Exception {  
        CamelContext context = super.createCamelContext();  
        context.setLoadTypeConverters(true);
        return context;  
    }  

}
