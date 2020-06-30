package org.jeets.dcs;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.protobuf.Jeets;
import org.jeets.protocol.JeetsRoute;
import org.jeets.protocol.JeetsClientProtocol;
import org.jeets.protocol.JeetsProtocol;
import org.jeets.protocol.util.Samples;
import org.junit.Test;
//import org.traccar.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JeetsDcsTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(JeetsDcsTest.class);

    @Test
    public void testJeetsDcsRoute() throws Exception {

//      pickup output: jpa.Device
        context.addRoutes(new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:jeets.model.traccar.jpa.Device")
                .log("jpa.Device: ${body}")
                .to("mock:result");
            }
        });
        MockEndpoint mock = getMockEndpoint("mock:result");

//      start test
        Jeets.Acknowledge response = (Jeets.Acknowledge) template
                .requestBody("netty:tcp://localhost:5200"
                        + "?clientInitializerFactory=#ack&sync=true", 
                        Samples.createDeviceWithPositionWithOneEvent());

//      evaluate and assert result/s
        LOG.info("client received response: " + response);
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
//      'jeets' must be registered before use
        String from = "netty:tcp://localhost:5200"
                + "?serverInitializerFactory=#jeets&sync=true";
        String routeId = "jeets";
        return new JeetsRoute(from, routeId);
    }

    @Override
    protected Registry createCamelRegistry() throws Exception {
        Registry registry = new SimpleRegistry();
        registry.bind("jeets", new JeetsProtocol(null));    // request to server
        registry.bind("ack", new JeetsClientProtocol(null)); // response to client
        return registry;
    }

    /** load typeConverters from config file */
    protected CamelContext createCamelContext() throws Exception {  
        CamelContext context = super.createCamelContext();  
        context.setLoadTypeConverters(true);
        return context;  
    }  

}
