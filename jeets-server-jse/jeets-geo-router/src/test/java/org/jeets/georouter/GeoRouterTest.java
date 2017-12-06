package org.jeets.georouter;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
        String routeName = "testDeviceToGeoTopics";
        
        context.addRoutes(new GeoBasedRoute() );
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:topic:hvv.device.in").to(testEndpoint);
                from("activemq:topic:gts.device.in").to(testEndpoint);
            }
        });
        
        testEndpoint.expectedMessageCount(3);
        testEndpoint.message(0).header("senddevice").isEqualTo("hvv");
        testEndpoint.message(1).header("senddevice").isEqualTo("gts");
        testEndpoint.message(2).header("senddevice").isEqualTo("gts");

        List<Position> positions = createTrack();
        List<Device> devices = divideTrack(positions);
        for (Device device : devices) {
            template.sendBody(GeoBasedRoute.startUri, device);
//            sleep
        }

        testEndpoint.assertIsSatisfied();
        context.stopRoute(routeName);
        context.removeRoute(routeName);
    }
    
    private List<Device> divideTrack(List<Position> positions) {
        List<Device> devices = new ArrayList<>();

        Device device = Samples.createDeviceEntity();
        Set<Position> positionSet = new HashSet<Position>();
        positionSet.add(positions.get(0));
        positionSet.add(positions.get(1));
        positionSet.add(positions.get(2));
        device.setPositions(positionSet);
        devices.add(device);
//      ---------- GeoFence ----------
        device = Samples.createDeviceEntity();
        positionSet = new HashSet<Position>();
        positionSet.add(positions.get(3));
        positionSet.add(positions.get(4));
        positionSet.add(positions.get(5));
        positionSet.add(positions.get(6));
        positionSet.add(positions.get(7));
        positionSet.add(positions.get(8));
        device.setPositions(positionSet);
        devices.add(device);

        device = Samples.createDeviceEntity();
        positionSet = new HashSet<Position>();
        positionSet.add(positions.get(9));
        positionSet.add(positions.get(10));
//      ---------- GeoFence ----------
        positionSet.add(positions.get(11));
        positionSet.add(positions.get(12));
        device.setPositions(positionSet);
        devices.add(device);
        
        return devices;
    }

    private List<Position> createTrack() {
        List<Position> positions = new ArrayList<>();
        positions.add(createTrackPoint("04.11.17 00:37:29", 53.56985d,  10.057684d, "Wandsbeker Chaussee"));
        positions.add(createTrackPoint("04.11.17 00:39:29", 53.567647d, 10.046565d, "Ritterstraße"));
        positions.add(createTrackPoint("04.11.17 00:41:29", 53.564706d, 10.035504d, "Wartenau"));
        positions.add(createTrackPoint("04.11.17 00:42:29", 53.559529d, 10.027395d, "Lübecker Straße"));
        positions.add(createTrackPoint("04.11.17 00:43:29", 53.556626d, 10.019024d, "Lohmühlenstraße"));
        positions.add(createTrackPoint("04.11.17 00:45:29", 53.55206d,  10.009756d, "Hauptbahnhof Süd"));
        positions.add(createTrackPoint("04.11.17 00:47:29", 53.549034d, 10.006214d, "Steinstraße"));
        positions.add(createTrackPoint("04.11.17 00:48:29", 53.547669d, 10.000825d, "Meßberg"));
        positions.add(createTrackPoint("04.11.17 00:50:29", 53.552546d,  9.993471d, "Jungfernstieg"));
        positions.add(createTrackPoint("04.11.17 00:51:29", 53.558853d,  9.989303d, "Stephansplatz (Oper/CCH)"));
        positions.add(createTrackPoint("04.11.17 00:54:29", 53.572764d,  9.989055d, "Hallerstraße"));
        positions.add(createTrackPoint("04.11.17 00:56:29", 53.581794d,  9.988088d, "Klosterstern"));
        positions.add(createTrackPoint("04.11.17 00:58:29", 53.588735d,  9.990741d, "Kellinghusenstraße"));
        LOG.info("Track has {} positions ", positions.size());
        return positions;
    }
    
    private Position createTrackPoint(
            String fixtime, double latitude, double longitude, String address) {
        Position pos = Samples.createPositionEntity();
        pos.setLatitude(latitude); pos.setLongitude(longitude);
//      pos.setFixtime(fixtime);pos.setAddress(address);
        return pos;
    }
    

    @Test
    public void testDeviceToGeoTopics() throws Exception {
        String routeName = "testDeviceToGeoTopics";
//      move to .createCamelContext for all tests:
        context.addRoutes(new GeoBasedRoute() );
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:topic:hvv.device.in").to(testEndpoint);
                from("activemq:topic:gts.device.in").to(testEndpoint);
            }
        });
        
        testEndpoint.expectedMessageCount(1);
        testEndpoint.message(0).header("senddevice").isEqualTo("gts");

        Device deviceIn = createDevice();
        template.sendBody(GeoBasedRoute.startUri, deviceIn);
//      template.sendBodyAndHeader(
//               GeoBasedRoute.startUri, deviceIn, "senddevice", "hamburg");

        testEndpoint.assertIsSatisfied();
        
        context.stopRoute(routeName);
        context.removeRoute(routeName);
    }
    
    private Device createDevice() {
//      Samples.createDeviceEntity();
        return Samples.createDeviceWithPositionWithTwoEvents();
//      return Samples.createDeviceWithTwoPositions();
    }

//  @Override
//  protected JndiRegistry createRegistry() throws Exception {
//  protected RouteBuilder createRouteBuilder() throws Exception {

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
