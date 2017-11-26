package org.jeets.georouter;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.util.Samples;
import org.junit.Before;
import org.junit.Test;

import org.apache.activemq.camel.component.ActiveMQComponent;

public class GeoRouterTest extends CamelTestSupport {
    
    private boolean routeTracing = true;

    @Test
    public void testDeviceToGeoTopics() throws Exception {
        String routeName = "GeoRoute";
        Device deviceIn = createDevice();
//      move to createCamelContext for all tests
        context.addRoutes(new GeoRouteS() );
//      geo coded output
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:topic:hvv.device.in")
                .to(testEndpoint);

//              from("activemq:topic:gts.device.in")
//              .to(testEndpointTwo);
            }
        });
        
        testEndpoint.expectedMessageCount(1);
        testEndpoint.message(0).header("senddevice").isEqualTo("hvv");
//      testEndpoint.message(0).body().isInstanceOf(Device.class);

//      template.sendBody(startPointUri, deviceIn);
//      use this before GeoRouteR implementation:
        template.sendBodyAndHeader(testEndpoint, deviceIn, "senddevice", "hvv");

        testEndpoint.assertIsSatisfied();
        
//      Exchange message = testEndpoint.getExchanges().get(0);
//      Device deviceOut = (Device) message.getIn().getBody();
//      assertEquals(1, deviceOut.getPositions().size());
//        THIS DOESNT WORK ? different exchange ? ..
//      assertEquals("senddevice", message.getIn().getHeader("hvv"));
        
        context.stopRoute(routeName);
        context.removeRoute(routeName);
    }
    
    private Device createDevice() {
//      create a device/s
//      Samples.createDeviceEntity();
//      add position/s feasible for testing
        
//      actual trace with real fixtimes (too complex coords)
//      "2017-05-20 15:49:01";49.03097993;12.10312854;407
//      "2017-05-20 15:52:57";49.02847401;12.10734587;370
//      "2017-05-20 15:54:57";49.02865676;12.11003339;383
//      "2017-05-20 15:56:58";49.03296471;12.11323104;381
//      "2017-05-20 15:58:58";49.03363147;12.12226451;392
//      "2017-05-20 16:02:55";49.03797380;12.13681046;388

//      temporarily
        return Samples.createDeviceWithPositionWithTwoEvents();
//      return Samples.createDeviceWithTwoPositions();
    }

//    protected RouteBuilder createRouteBuilder() throws Exception {
//        return new RouteBuilder() {
//            public void configure() throws Exception {
//                from(testStartUri)
//                .to(testEndUri);
//            }
//        };
//    }

    protected String testUri = "mock:result";
    protected MockEndpoint testEndpoint;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        testEndpoint = getMockEndpoint(testUri);
    }

    /**
     * add ActiveMQComponent to CamelContext with main method
     */
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
        ActiveMQComponent activeMQComponent = Main.getAmqComponent(true);
        camelContext.addComponent("activemq", activeMQComponent);
        camelContext.setTracing(routeTracing);
        return camelContext;
    }

}
