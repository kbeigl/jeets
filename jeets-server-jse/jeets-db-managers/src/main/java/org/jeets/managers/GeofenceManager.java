package org.jeets.managers;

import java.util.ArrayList;
import java.util.List;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.DeviceGeofence;
import org.jeets.model.traccar.jpa.Event;
import org.jeets.model.traccar.jpa.Geofence;
import org.jeets.model.traccar.jpa.Position;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Geofence Manager is a plain Java implementation and does not rely on
 * Spring, Camel or JPA. Parameters are standardized to the System by using
 * System Entities from the PU in order to be used in various environments. For
 * a more complex and multi purpose manager you can export it to a Maven
 * project.
 */
public class GeofenceManager {

    private static final Logger LOG = LoggerFactory.getLogger(GeofenceManager.class);

    public void analyzeGeofences(Device msgDevice, Device gtsDevice) {
        LOG.info("analyzeGeofences message with {} against entity with {} positions", 
                msgDevice.getPositions().size(), gtsDevice.getPositions().size());
        List<DeviceGeofence> geofenceList = new ArrayList<DeviceGeofence>(gtsDevice.getDeviceGeofences());
        for (int gf = 0; gf < geofenceList.size(); gf++) {
            Geofence geofence = geofenceList.get(gf).getGeofence();
            analyzeGeofence(gtsDevice, msgDevice, geofence);
        }
    }

    private void analyzeGeofence(Device dbDevice, Device msgDevice, Geofence geofence) {
//      get latest position from database (reverse chrono order)
        Position lastPosition = dbDevice.getPositions().get(0);
        Polygon  jtsPolygon = (Polygon) createJtsGeometry(geofence.getArea());
        List<Position> msgPositions = msgDevice.getPositions();
        for (int pos = 0; pos < msgPositions.size(); pos++) {
            Position msgPosition = msgPositions.get(pos);
//          may be more effective to create full line and traverse JTS coordinates?
            String edge = "LINESTRING ("
                    + lastPosition.getLongitude() + " " + lastPosition.getLatitude() + ", "
                    +  msgPosition.getLongitude() + " " +  msgPosition.getLatitude() + ")";
            LineString jtsLine = (LineString) createJtsGeometry(edge);

            if (jtsLine.crosses(jtsPolygon)) {
                Event geofenceEvent = addGeofenceEvent(jtsLine, jtsPolygon);
                if (geofenceEvent != null) {
//                  create relations
                    geofenceEvent.setGeofenceid(geofence.getId());
                    geofenceEvent.setDevices(dbDevice);
                }
            }
//          progress to next edge
            lastPosition = msgPositions.get(pos);
        }
    }

//  temporary solution for testing with Traccar > refactor, move ...
    public final String TYPE_GEOFENCE_ENTER = "geofenceEnter";
    public final String TYPE_GEOFENCE_EXIT  = "geofenceExit";

    private Event addGeofenceEvent(LineString jtsLine, Polygon jtsPolygon) {
        LOG.debug("linestring " + jtsLine);
        Point startPoint = jtsLine.getStartPoint();
        Point endPoint   = jtsLine.getEndPoint();
        Point crossPoint = (Point) jtsPolygon.getBoundary().intersection(jtsLine);
        LOG.info("line from " + startPoint + " to " + endPoint + " intersects Geofence at " + crossPoint);

        if (startPoint.coveredBy(jtsPolygon)) {
            LOG.info("Event: " + TYPE_GEOFENCE_EXIT);
            Event exitEvent = new Event();
            exitEvent.setType(TYPE_GEOFENCE_EXIT);
//            exitEvent.setPositionid(positionid); ??
//            exitEvent.setServertime(servertime); ??
            return exitEvent;
//          new Position() .. ? -> add option !
//            distances ratio > time ratio + crossPoint coordinates
        } else if (endPoint.coveredBy(jtsPolygon)) {
            LOG.info("Event: " + TYPE_GEOFENCE_ENTER);
            return null;
        }
        return null;
    }

    private Geometry createJtsGeometry(String wktString) {
        Geometry geo = null;
        try {
            geo = new WKTReader().read(wktString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return geo;
    }

}
