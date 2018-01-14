package org.jeets.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.DeviceGeofence;
import org.jeets.model.traccar.jpa.DeviceGeofenceId;
import org.jeets.model.traccar.jpa.Geofence;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.model.traccar.util.Samples;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * Plain JUnit test of the GeofenceManager without database.
 */
public class GeofenceManagerTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(GeofenceManagerTest.class);

    @Test
    public void testGeofenceManager() throws Exception {
//      compare org.jeets.georouter.JtsTest
//      prepare test track with three fractions
        List<Position> positions = Samples.createU1Track();
        assertEquals(13, positions.size());
        List<Device> devices = Samples.divideU1Track(positions);

//      assume the first part of the track and a geofence is already persisted
        assertChronologicalOrder(devices.get(0).getPositions(), true);
        Device dbDevice = simulateDatabaseLookup(devices.get(0));
        assertEquals(3, dbDevice.getPositions().size());
        assertChronologicalOrder(dbDevice.getPositions(), false);
        List<DeviceGeofence> list = new ArrayList<DeviceGeofence>(dbDevice.getDeviceGeofences());
        assertEquals(wktHvvPolygon, list.get(0).getGeofence().getArea());

//      now the second part of the track arrives as a message
        Device inDevice = devices.get(1);
        inDevice.setId(deviceId);

        GeofenceManager gfManager = new GeofenceManager();
        gfManager.analyzeGeofences(inDevice, dbDevice);
        
//      assert created events !!

//      update dbDevice positions and order
        List<Position> reversedMsgs = revertChronologicalOrder(inDevice.getPositions());
        if (reversedMsgs.addAll(dbDevice.getPositions()))
            dbDevice.setPositions(reversedMsgs);
//      assert dbDevice positions and order
        assertEquals(9, dbDevice.getPositions().size());
        assertChronologicalOrder(dbDevice.getPositions(), false);
        
//      now the third part of the track arrives as a message
        inDevice = devices.get(2);
        inDevice.setId(deviceId);
        gfManager.analyzeGeofences(inDevice, dbDevice);
        
//      assertions ...
        
    }

    private int deviceId = 456;  // random

    /**
     * Sample method to use the Persistence Unit's Entities and -relations
     * without database. The device's track is assumed to be retrieved from the
     * database and a virtually persisted Geofence is added via relations.
     */
    private Device simulateDatabaseLookup(Device device) {
        device.setId(deviceId);
        Geofence geofence = new Geofence(248, "downtown Hamburg", wktHvvPolygon);
        DeviceGeofenceId deviceGeofenceId = new DeviceGeofenceId(device.getId(), geofence.getId());
        DeviceGeofence   deviceGeofence   = new DeviceGeofence(deviceGeofenceId, device, geofence);
        Set<DeviceGeofence> deviceGeofences = new HashSet<DeviceGeofence>();
        deviceGeofences.add(deviceGeofence);
        device.setDeviceGeofences(deviceGeofences);
        device.setPositions(revertChronologicalOrder(device.getPositions()));
        return device;
    }

//  CAUTION! Subsequent fixtimes could also be equal depending on the system logic!
    private void assertChronologicalOrder(List<Position> positionList, boolean forward) {
        for (int pos = 0; pos < positionList.size() - 1; pos++)
            if (forward)
                assertTrue(positionList.get(pos).getFixtime().before(positionList.get(pos + 1).getFixtime()));
            else
                assertTrue(positionList.get(pos).getFixtime().after(positionList.get(pos + 1).getFixtime()));
    }

    private List<Position> revertChronologicalOrder(List<Position> positionList) {
        List<Position> reversePositionList = positionList.subList(0, positionList.size());
        Collections.reverse(reversePositionList);
        return reversePositionList;
    }

    // NOTE! lat and lon are swapped in Traccar representation!!
    private String wktHvvPolygon = "POLYGON((" // x-lon, y-lat
            + " 9.989269158916906 53.57541694442838 ,  9.998318508390481 53.55786417233634, "
            + "10.037949531036517 53.562496767906936, 10.021498582439857 53.5451640563584, "
            + "10.00793733366056  53.54138991423747 ,  9.985634869615584 53.540103457327746, "
            + " 9.970257661419355 53.54332802925086 ,  9.965966126995527 53.55373112988562, "
            + " 9.989269158916906 53.57541694442838 ))";

}
