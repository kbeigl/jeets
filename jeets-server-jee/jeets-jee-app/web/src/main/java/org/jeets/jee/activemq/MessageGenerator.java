package org.jeets.jee.activemq;

import java.util.Arrays;
import java.util.Date;

import javax.inject.Named;

import org.apache.camel.CamelContext;
//import org.jeets.model.traccar.jpa.Device;
//import org.jeets.model.traccar.util.Samples;
import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Position;

/**
 * Simple message generator to simulate incoming device message String!
 * (for the time being)
 * 
 * @author kbeigl@jeets.org
 */
@Named
public class MessageGenerator {

    public String generateMessageString(CamelContext camelContext) {
        return "49.123 12.456";
    }
    
    public Device generateDeviceMessage(CamelContext camelContext) {
        Device dev = createDeviceWithTwoPositions();
        System.out.println("created device: " + dev);
        return dev;
    }
    
//  home position: *7* Obere Regenstraße, Regensburg, BY, DE
    private static double lat = 49.03091228d;
    private static double lon = 12.10282818d;
    private static final String unique = "395389";
    
    private Device createDeviceWithTwoPositions() {
        Device device = createDeviceEntity();
        Position pos1 = createPositionEntity();
        pos1.setDevice(device);
        Position pos2 = createPositionEntity();
//      *8* Obere Regenstraße, Regensburg, BY, DE
        pos2.setLatitude(49.03107129d);
        pos2.setLongitude(12.10331786d);
        pos2.setDevice(device);
        device.setPositions(Arrays.asList(pos1,pos2));
//      device.setPositions(new HashSet<Position>(Arrays.asList(pos1,pos2)));
        return device;
    }

    private Device createDeviceEntity() {
        Device device = new Device();
        device.setUniqueid(unique);
        device.setName("DeviceEntitySample");
        device.setLastupdate(new Date());
//      device.setPosition(position);
//      device.setAttributeAliases(attributeAlias);
//      device.setAttributes(attributes);
//      device.setCategory(category);
//      device.setContact(contact);
//      device.setDeviceGeofences(deviceGeofences);
//      device.setGroups(groups);
//      device.setModel(model);
//      device.setPhone(phone);
//      device.setUserDevices(userDevices);
        return device;
    }
    
    private Position createPositionEntity() {
        Position position = new Position();
        position.setValid(true);
        position.setLatitude(lat);
        position.setLongitude(lon);
        position.setFixtime(new Date());
        position.setDevicetime(new Date());
        position.setServertime(new Date());
        position.setProtocol("protocol");
        
//      position.setDevice(device);
//      position.setAccuracy(accuracy);
//      position.setAddress(address);
//      position.setAltitude(altitude);
//      position.setAttributes(attributes);
//      position.setCourse(course);
//      position.setEvents(events);
//      position.setNetwork(network);
//      position.setSpeed(speed);
        return position;
    }

}
