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

    @Override
    public void process(Exchange exchange) throws Exception {
        String headerName = "senddevice";
        String headerValue = "gts";
        String sendTo = "hvv";

        Device device = (Device) exchange.getIn().getBody();
        List<Position> positions = new ArrayList<Position>(device.getPositions());
        LOG.info("Device message has {} positions", positions.size());

        if (positions.size() > 0) { // noop
            Polygon hvvPolygon = (Polygon) createJtsGeometry(wktHvvPolygon); 
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
     * Various JTS methods to relate geometries.
     */
    private boolean geometriesRelate(Geometry geoA, Geometry geoB) {
//        LOG.info("relate A " + geoA.toText());
//        LOG.info("    to B " + geoB.toText());
        LOG.info("disjoint   " + geoA.disjoint(geoB) + "\t" + geoB.disjoint(geoA));
        return geoA.disjoint(geoB);
//        return false;
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

//  NOTE! lat and lon are swapped in Traccar representation!!
    private String wktHvvPolygon = "POLYGON((" // x-lon, y-lat
            + " 9.989269158916906 53.57541694442838 ,  9.998318508390481 53.55786417233634, "
            + "10.037949531036517 53.562496767906936, 10.021498582439857 53.5451640563584, "
            + "10.00793733366056  53.54138991423747 ,  9.985634869615584 53.540103457327746, "
            + " 9.970257661419355 53.54332802925086 ,  9.965966126995527 53.55373112988562, "
            + " 9.989269158916906 53.57541694442838 ))";

    /**
     * validation geometry can be created dynamically (external resource..)
     */
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
