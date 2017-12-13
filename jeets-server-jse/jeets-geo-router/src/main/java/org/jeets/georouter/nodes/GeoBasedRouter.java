package org.jeets.georouter.nodes;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Route device messages to Hamburg HVV or otherwise GTS.
 * Apply JTS for agencies boundaries.
 * 
 * @author kbeigl@jeets.org
 */
public class GeoBasedRouter implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(GeoBasedRouter.class);

    public GeoBasedRouter(String wktGeometry, String targetTopic) {
        referencePolygon = (Polygon) createJtsGeometry(wktGeometry);
        sendTo = targetTopic;
    }

    private Polygon referencePolygon;
    private String sendTo = "testtopic";

    @Override
    public void process(Exchange exchange) throws Exception {
        String headerName = "senddevice", headerValue = "gts";

        Device device = (Device) exchange.getIn().getBody();
        List<Position> positions = new ArrayList<Position>(device.getPositions());
        if (positions.size() > 0) { // noop
            Polygon hvvPolygon = referencePolygon; 
            if (positions.size() == 1) {
                Point devicePoint = createPoint(positions.get(0));
                if (geometriesRelate(hvvPolygon, devicePoint)) {
                    headerValue = sendTo;
                }
            } else if (positions.size() > 1) {
                LineString deviceTrack = createLinestring(positions);
                if (geometriesRelate(hvvPolygon, deviceTrack)) {
                    headerValue = sendTo;
                }
            }
        }

        exchange.getIn().setHeader(headerName, headerValue);
    }

    /**
     * Various JTS methods to relate geometries for different use cases.
     */
    private boolean geometriesRelate(Geometry geoA, Geometry geoB) {
        if (geoA.disjoint(geoB))   return false;
        if (geoA.intersects(geoB)) return true;
        if (geoA.contains(geoB)) return true;
        if (geoA.crosses(geoB)) return true;
        if (geoA.covers(geoB)) return true;
//      order!!
        if (geoB.within(geoA)) return true;
        
        return false;
    }

    private Point createPoint(Position position) {
        return new GeometryFactory().createPoint(
                new Coordinate(position.getLongitude(), position.getLatitude()));
    }

    private LineString createLinestring(List<Position> positions) {
        Coordinate[] coordinates = new Coordinate[positions.size()];
        int counter = 0;
        for (Position pos : positions) {
            coordinates[counter++] = new Coordinate(pos.getLongitude(), pos.getLatitude());
        }
        return new GeometryFactory().createLineString(coordinates);
    }
    
    private Geometry createJtsGeometry(String wktString) {
        Geometry jtsGeo = null;
        try {
            jtsGeo = new WKTReader().read(wktString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jtsGeo;
    }

}
