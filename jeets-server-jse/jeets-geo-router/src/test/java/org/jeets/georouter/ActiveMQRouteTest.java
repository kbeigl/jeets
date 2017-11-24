package org.jeets.georouter;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.util.Samples;
import org.junit.Before;
import org.junit.Test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;

public class ActiveMQRouteTest extends CamelTestSupport {
    
    private boolean tracing = true;
    protected MockEndpoint testEndpoint;
    protected String component = "activemq:", mqType = "queue:", inbox = "device.in",
            startEndpointUri = component + mqType + inbox, testEndpointUri = "mock:result";

    @Test
    public void testDeviceToTileRouter() throws Exception {
        
//        how to send 
//        org.jeets.protocol.util.Samples.createDeviceWithPositionWithOneEvent()
//        to MQ device.in ?
        
    }

    @Test
    public void testAmqRouteWithDeviceMessage() throws Exception {
//      send jpa Entity (not Protobuffer!)
        Device device = Samples.createDeviceEntity();
//      Samples.createDeviceWithPositionWithTwoEvents();
//      Samples.createDeviceWithTwoPositions();
//      create test cases
        testEndpoint.expectedMessageCount(1);
        testEndpoint.message(0).header("cheese").isEqualTo(123);
        testEndpoint.message(0).body().isInstanceOf(Device.class);
//      send message
        template.sendBodyAndHeader(startEndpointUri, device, "cheese", 123);
//        template.sendBody(startEndpointUri, device);
//      validate test cases
        testEndpoint.assertIsSatisfied();
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from(startEndpointUri)
                .to("activemq:queue:test.b");
                from("activemq:queue:test.b")
                .to(testEndpointUri);
            }
        };
    }

    /**
     * get Endpoint for testing
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        testEndpoint = (MockEndpoint) context.getEndpoint(testEndpointUri);
    }

    /**
     * register ActiveMQComponent with main method
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
        camelContext.setTracing(tracing);
        return camelContext;
    }

}
