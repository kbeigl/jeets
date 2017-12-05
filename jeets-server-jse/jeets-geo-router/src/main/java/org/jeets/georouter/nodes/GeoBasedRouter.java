package org.jeets.georouter.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
public class GeoBasedRouter  implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(GeoBasedRouter.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        String header = "gts"; // default
        Device device = (Device) exchange.getIn().getBody();
        List<Position> positions = new ArrayList<Position>(device.getPositions());
        LOG.info("Device message has {} positions", positions.size());

        Geometry deviceGeo = null;
        if (positions.size() > 0) { // noop
            if (positions.size() == 1) {
                deviceGeo = createPoint(positions.get(0));
            } else if (positions.size() > 1) {
                deviceGeo = createLinestring(positions);
            }
            if (relateGeometries(deviceGeo))
                exchange.getIn().setHeader("senddevice", "hvv");
        }

        exchange.getIn().setHeader("senddevice", header);
    }

    /**
     * Various JTS methods to relate geometries.
     */
    private boolean relateGeometries(Geometry deviceGeo) {
        if (hvvGeometry == null)
            hvvGeometry = createHvvPolygon(); 

        // Geometry relationships are represented by the 
        // following functions returning true or false:
//      if (hvvGeometry.disjoint(deviceGeo)) return false;

//        touches(Geometry) - geometry have to just touch, crossing or overlap will not work
//        intersects(Geometry)
//        crosses(Geometry)
//        within(Geometry) - geometry has to be full inside
//        overlaps(Geometry) - has to actually overlap the edge, being within or touching will not work
//        covers(Geometry)
//        coveredBy(Geometry)
//        relate(Geometry, String) - allows general check of relationship see dim9 page
//        relate(Geometry)

        // too strict (false for all track sections)
        if (hvvGeometry.contains(deviceGeo))
            return true;
        
        
        
        LOG.info("hvvGeometry does not contain deviceGeo");
        return false;
    }

    private Point createPoint(Position position) {
        return new GeometryFactory().createPoint(
                new Coordinate(position.getLongitude(), position.getLatitude()));
    }

    private Geometry createLinestring(List<Position> positions) {
        Coordinate[] coordinates = new Coordinate[positions.size()];
        int counter = 0;
        for (Position pos : positions) {
            coordinates[counter++] = new Coordinate(pos.getLatitude(), pos.getLongitude());
        }
        return new GeometryFactory().createLineString(coordinates);
    }

//  WARNING! lat and lon are swapped in Traccar representation!!
    private String wktHvvPolygon = "POLYGON((" // x-lon, y-lat
            + " 9.989269158916906 53.57541694442838 ,  9.998318508390481 53.55786417233634, "
            + "10.037949531036517 53.562496767906936, 10.021498582439857 53.5451640563584, "
            + "10.00793733366056  53.54138991423747 ,  9.985634869615584 53.540103457327746, "
            + " 9.970257661419355 53.54332802925086 ,  9.965966126995527 53.55373112988562, "
            + " 9.989269158916906 53.57541694442838 ))";
    Geometry hvvGeometry;
    
    private Polygon createHvvPolygon() {
        try {
            hvvGeometry = new WKTReader().read(wktHvvPolygon);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (Polygon) hvvGeometry;
    }

}
