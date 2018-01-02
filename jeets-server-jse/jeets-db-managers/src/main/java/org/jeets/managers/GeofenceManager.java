package org.jeets.managers;

import org.jeets.model.traccar.jpa.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeofenceManager {

    private static final Logger LOG = LoggerFactory.getLogger(GeofenceManager.class);
    
    public void analyzeGeofences(Device inDevice, Device gtsDevice) {
        
        LOG.info("analyzeGeofences for message with {} and entity with {} positions"
                , inDevice.getPositions().size(), gtsDevice.getPositions().size());
        
    }

}
