package org.jeets.managers;

import java.util.ArrayList;
import java.util.List;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.DeviceGeofence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Geofence Manager is a plain Java implementation and does not rely on
 * Spring, Camel or JPA. Parameters are standardized to the System by using
 * System Entities from the PU in order to be used in various environments. For
 * a more complex and multi purpose manager you can export this one to a Maven
 * project.
 */
public class GeofenceManager {

    private static final Logger LOG = LoggerFactory.getLogger(GeofenceManager.class);

    public Device analyzeGeofences(Device inDevice, Device gtsDevice) {
        LOG.info("analyzeGeofences message with {} against entity with {} positions"
                , inDevice.getPositions().size(), gtsDevice.getPositions().size());

//      TODO: traverse complete list
        List<DeviceGeofence> list = new ArrayList<DeviceGeofence>(gtsDevice.getDeviceGeofences());
        String wktPolygon = list.get(0).getGeofence().getArea();
        LOG.info(wktPolygon);

//      create linestring (from n to n+1 ?)
//      JTS analysis > determine exact intersection and create Event (and Position!?)

        return gtsDevice;  // with added information !
    }

}
