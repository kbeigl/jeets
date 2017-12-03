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
        String header = "gts";  // default
        Device device = (Device) exchange.getIn().getBody();
        List<Position> positions = new ArrayList<Position>(device.getPositions());
        LOG.info("Device message has {} positions", positions.size());
        
        LOG.info("create JTS geometries ..");
//      if (positions.size() < 1) { // do nothing
        if (positions.size() == 1) {
            Point devPoint = createPoint( positions.get(0) );
            
        } else if (positions.size() > 1) {
            LineString devTrack = createLinestring( device );
            
        }
        
        if (relateDeviceToGeometry(device))
            exchange.getIn().setHeader("senddevice", "hvv");
        
        exchange.getIn().setHeader("senddevice", header);
    }

    private Point createPoint(Position position) {
        return new GeometryFactory().createPoint(
                new Coordinate(position.getLongitude(), position.getLatitude()));
    }

    private LineString createLinestring(Device device) {

//      TODO: LINESTRING from at least two Positions in Device
//      Coordinate[] coordinates = new Coordinate[] {
//      new Coordinate(0, 0), new Coordinate(10, 10), new Coordinate(20, 20) };
//      Geometry lineGeo = new GeometryFactory().createLineString(coordinates);
    
        return null;
    }

    //  improve method terminology
    private boolean relateDeviceToGeometry(Device device) {
//      create once at object creation time
        Polygon hvvPolygon = createHvvPolygon(); 
//        hvvPolygon.
//      Geometry g3 = hvvPolygon.intersection(coordGeo);

        Set<Position> positions = device.getPositions();
        Position position = (Position) positions.toArray()[ positions.size()-1 ];
        LOG.info("Position: " + position.getLongitude() + " ," + position.getLatitude());
        
        Coordinate coord = new Coordinate(position.getLongitude(), position.getLatitude());
        Geometry coordGeo = new GeometryFactory().createPoint(coord);
        
        if (hvvPolygon.contains(coordGeo))
            return true;
        
        return false;
    }

    private Polygon createHvvPolygon() {
//      WARNING! lat and lon are swapped in Traccar representation!!
//      workaround: create poly(lat,lon), then create poly from coord loop and swap there
        String wktHvvPolygon = "POLYGON(("  // x-lon, y-lat
                + " 9.989269158916906 53.57541694442838 ,  9.998318508390481  53.55786417233634, "
                + "10.037949531036517 53.562496767906936, 10.021498582439857  53.5451640563584, "
                + "10.00793733366056  53.54138991423747 ,  9.985634869615584  53.540103457327746, "
                + " 9.970257661419355 53.54332802925086 ,  9.965966126995527  53.55373112988562, "
                + " 9.989269158916906 53.57541694442838 ))";
        
//      Geometry could be skipped (cast directly to Polygon)
        Geometry hvvGeometry = null;
        try {
            hvvGeometry = new WKTReader().read(wktHvvPolygon);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (Polygon) hvvGeometry;
    }

}
