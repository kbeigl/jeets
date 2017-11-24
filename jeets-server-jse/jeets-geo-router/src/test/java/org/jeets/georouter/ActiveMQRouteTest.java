package org.jeets.georouter;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.georouter.steps.TileMapper;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.util.Samples;
import org.junit.Before;
import org.junit.Test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;

public class ActiveMQRouteTest extends CamelTestSupport {
    
    private boolean routeTracing = true;
    protected MockEndpoint testEndpoint;
    protected String component = "activemq:", mqType = "queue:", inbox = "device.in",
            startEndpointUri = component + mqType + inbox, testEndUri = "mock:result",
            testStartUri = "activemq:queue:test.in";

    @Test
    public void testDeviceToTileMapper() throws Exception {
        String routeName = "TileRoute";
        Device deviceIn = createDevice();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(startEndpointUri)
                .routeId(routeName)
                .process(new TileMapper())
                
//              now route recipient list
                
                .to(testStartUri);
            }
        });
        
        testEndpoint.expectedMessageCount(1);
        template.sendBody(startEndpointUri, deviceIn);
        testEndpoint.assertIsSatisfied();
        
        Exchange message = testEndpoint.getExchanges().get(0);
        Device deviceOut = (Device) message.getIn().getBody();
        assertEquals(1, deviceOut.getPositions().size());
        
//      last position (49.03091228,12.10282818) maps to tiles ..
//      TileMapper.getTileString(lat, lon, zoom)
        assertEquals("z13x4371y2812", message.getIn().getHeader("tileZ13"));
        assertEquals("z14x8742y5624", message.getIn().getHeader("tileZ14"));
        assertEquals("z15x17485y11248", message.getIn().getHeader("tileZ15"));
        assertEquals("z16x34971y22497", message.getIn().getHeader("tileZ16"));
        
        context.stopRoute(routeName);
        context.removeRoute(routeName);
    }

    @Test
    public void testAmqRouteWithDeviceMessage() throws Exception {
        Device device = createDevice();
//      create test cases
        testEndpoint.expectedMessageCount(1);
//      test header creation
        testEndpoint.message(0).header("tile").isEqualTo("x234y421");
        testEndpoint.message(0).body().isInstanceOf(Device.class);
//      send message
        template.sendBodyAndHeader(testStartUri, device, "tile", "x234y421");
//        template.sendBody(startEndpointUri, device);
//      validate test cases
        testEndpoint.assertIsSatisfied();
    }
    
    private Device createDevice() {
//      return Samples.createDeviceEntity();    // 0 positions
//      (49.03091228,12.10282818)
        return Samples.createDeviceWithPositionWithTwoEvents();
//      return Samples.createDeviceWithTwoPositions();
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from(testStartUri)
                .to("activemq:queue:test.b");
                from("activemq:queue:test.b")
                .to(testEndUri);
            }
        };
    }

    /**
     * get Endpoint for testing
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
//      testEndpoint = (MockEndpoint) context.getEndpoint(testEndUri);
        testEndpoint = getMockEndpoint(testEndUri);
    }

    /**
     * add ActiveMQComponent to CamelContext with main method
     */
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
//      we need to add trusted packages
        ActiveMQConnectionFactory activeMqConnectionFactory = 
                Main.getConnectionFactory(true);
        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(activeMqConnectionFactory);
//        ConnectionFactory connectionFactory = activeMqConnectionFactory;
//        activeMQComponent.setConnectionFactory(connectionFactory);
        camelContext.addComponent("activemq", activeMQComponent);
        camelContext.setTracing(routeTracing);
        return camelContext;
    }

}
