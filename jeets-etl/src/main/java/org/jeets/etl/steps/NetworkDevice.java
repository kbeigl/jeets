package org.jeets.etl.steps;

import org.jeets.model.traccar.jpa.Device;

/**
 * This is a wrapper for a Device Entity converted by the DCS and still missing
 * some required database fields. By introducing a new Type to wrap a Device
 * Entity we can implicitly use a Type Converter which provides the Entity
 * Manager from the JPA Component to enrich the NetworkDevice to a (managed)
 * Device Entity. After looking up the Device in the database it can be
 * persisted (or merged) thereafter.
 * 
 * @author kbeigl@jeets.org
 */
public class NetworkDevice {

    /** The incomplete Device (from the DCS Conversion) */
    private Device device;

    public NetworkDevice(Device netDevice) {
        device = netDevice;
    }

    public Device getDevice() {
        return device;
    }
    
}
