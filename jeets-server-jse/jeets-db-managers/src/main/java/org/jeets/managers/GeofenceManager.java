package org.jeets.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
        LOG.debug("analyzeGeofences message with {} against entity with {} positions", 
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
                Event geofenceEvent = analyzeGeofenceEvent(jtsLine, jtsPolygon);
                if (geofenceEvent != null) {
                    
                    if (geofenceEvent.getType().equals(TYPE_GEOFENCE_ENTER))
                        geofenceEvent.setPosition(msgPosition);
                    else if (geofenceEvent.getType().equals(TYPE_GEOFENCE_EXIT))
                        geofenceEvent.setPosition(lastPosition);
                    
//                  see traccar timestamp deviation ~ms <> pos.servertime
                    geofenceEvent.setServertime(new Date()); 
                    geofenceEvent.setDevice(dbDevice);
                    geofenceEvent.setGeofence(geofence);
                    Set<Event> dbDeviceEvents = dbDevice.getEvents();
                    dbDeviceEvents.add(geofenceEvent);
                    dbDevice.setEvents(dbDeviceEvents);
                }
            }
//          shift to next edge
            lastPosition = msgPositions.get(pos);
        }
    }

//  temporary solution for testing with Traccar 
//    > refactor, move ... compare pu.Samples with enums
    public static final String TYPE_GEOFENCE_ENTER = "geofenceEnter";
    public static final String TYPE_GEOFENCE_EXIT  = "geofenceExit";

    private Event analyzeGeofenceEvent(LineString jtsLine, Polygon jtsPolygon) {
        LOG.debug("linestring " + jtsLine);
        Point startPoint = jtsLine.getStartPoint();
        Point endPoint   = jtsLine.getEndPoint();
        Point crossPoint = (Point) jtsPolygon.getBoundary().intersection(jtsLine);
        LOG.debug("line from {} to {} intersects Geofence at {}", startPoint, endPoint, crossPoint);

        if (startPoint.coveredBy(jtsPolygon)) {
            Event exitEvent = new Event();
            exitEvent.setType(TYPE_GEOFENCE_EXIT);
//          if (generateGeofenceEventPosition()) {
//              new Position() .. ? -> add option !
//              distances ratio > time ratio + crossPoint coordinates
//          }   
            return exitEvent;
        } else if (endPoint.coveredBy(jtsPolygon)) {
            Event enterEvent = new Event();
            enterEvent.setType(TYPE_GEOFENCE_ENTER);
//          if (generateGeofenceEventPosition()) {
//              new Position() .. ? -> add option !
//              distances ratio > time ratio + crossPoint coordinates
//          }   
            return enterEvent;
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
