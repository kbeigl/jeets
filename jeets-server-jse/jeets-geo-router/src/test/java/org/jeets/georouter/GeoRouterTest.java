package org.jeets.georouter;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.model.traccar.util.Samples;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.activemq.camel.component.ActiveMQComponent;

public class GeoRouterTest extends CamelTestSupport {
    
    private static final Logger LOG = LoggerFactory.getLogger(GeoRouterTest.class);
    private boolean routeTracing = false;
    
    @Test
    public void testTrackToGeoTopics() throws Exception {
//      String routeName = "testDeviceToGeoTopics";
        
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:topic:hvv.device.in").to(testEndpoint);
                from("activemq:topic:gts.device.in").to(testEndpoint);
            }
        });
        
        testEndpoint.expectedMessageCount(3);
//      GeoBasedRouter is under development -> adjust test result
        testEndpoint.message(0).header("senddevice").isEqualTo("gts");
        testEndpoint.message(1).header("senddevice").isEqualTo("hvv");
        testEndpoint.message(2).header("senddevice").isEqualTo("hvv");

        List<Position> positions = Samples.createU1Track();
        List<Device> devices = Samples.divideU1Track(positions);
        for (Device device : devices) {
            template.sendBody(GeoBasedRoute.startUri, device);
        }

        testEndpoint.assertIsSatisfied();
//        context.stopRoute(routeName);
//        context.removeRoute(routeName);
    }

    @Test
    public void testDeviceToGeoTopics() throws Exception {
        String routeName = "testDeviceToGeoTopics"; // ??
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:topic:hvv.device.in").to(testEndpoint);
                from("activemq:topic:gts.device.in").to(testEndpoint);
            }
        });
        
        testEndpoint.expectedMessageCount(1);
//      GeoBasedRouter is under development -> adjust test result
        testEndpoint.message(0).header("senddevice").isEqualTo("gts");

        Device deviceIn = createDevice();
        template.sendBody(GeoBasedRoute.startUri, deviceIn);

        testEndpoint.assertIsSatisfied();
        
        context.stopRoute(routeName);
        context.removeRoute(routeName);
    }
    
    private Device createDevice() {
//      Samples.createDeviceEntity();
//      Samples.createDeviceWithTwoPositions();
        return Samples.createDeviceWithPositionWithTwoEvents();
    }

//  NOTE! lat and lon are swapped in Traccar representation!!
    private String wktHvvPolygon = "POLYGON((" // x-lon, y-lat
            + " 9.989269158916906 53.57541694442838 ,  9.998318508390481 53.55786417233634, "
            + "10.037949531036517 53.562496767906936, 10.021498582439857 53.5451640563584, "
            + "10.00793733366056  53.54138991423747 ,  9.985634869615584 53.540103457327746, "
            + " 9.970257661419355 53.54332802925086 ,  9.965966126995527 53.55373112988562, "
            + " 9.989269158916906 53.57541694442838 ))";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new GeoBasedRoute(wktHvvPolygon, "hvv");
    }

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
