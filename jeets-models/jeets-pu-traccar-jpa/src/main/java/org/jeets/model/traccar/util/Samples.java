package org.jeets.model.traccar.util;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Event;
import org.jeets.model.traccar.jpa.Position;

public class Samples {

//  home position: *7* Obere Regenstraße, Regensburg, BY, DE
    public static double lat = 49.03091228d;
    public static double lon = 12.10282818d;
    public static final String unique = "395389";
    // server event types
    public static final String deviceMoving ="deviceMoving"; 
    public static final String deviceStopped ="deviceStopped"; 
//  TODO: client event types, see protocol Samples
//  motionEvent = Traccar.EventType.KEY_MOTION,
//   alarmEvent = Traccar.EventType.KEY_ALARM;
//   .. Traccar.AlarmType.ALARM_SOS;

    public static Device createDeviceWithPositionWithTwoEvents() {
        Device device = createDeviceEntity();
        Position position = createPositionEntity();
        position.setDevice(device);
//      investigate further ..
        position.setDevice(device);
        device.setPositions(Arrays.asList(position));
//      device.setPositions(new HashSet<Position>(Arrays.asList(position)));
//      latest (GPS time!) position of list / or database (?) 
//      can only be set via database-ID lookup!
//      device.setPositionid(positionid);

        Event moveEvent = createMovingEventEntity();
        moveEvent.setType(deviceMoving);
        Event stopEvent = createMovingEventEntity();
        stopEvent.setType(deviceStopped);
//      can only be set via database-ID lookup!
//      stopEvent.setPositionid(positionid);
//      nice to have (not so nice to implement in model) ..
//      stopEvent.setPosition(position);
//      position.setEvents(new HashSet<Event>(Arrays.asList(moveEvent, stopEvent)));
        device.setEvents(new HashSet<Event>(Arrays.asList(stopEvent, moveEvent)));

        return device;
    }

    /*
     * todo: add test case
     * nice to have: createDeviceWithRoute( nrOfPositions )
     * prepare file to place in resources (json?)
     */
    public static Device createDeviceWithTwoPositions() {
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

    public static Device createDeviceEntity() {
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
    
    public static Position createPositionEntity() {
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
    
    public static Event createMovingEventEntity() {
        Event event = new Event();
//      event.setAttributes(attributes);
//      event.setGeofenceid(geofenceid);
//      event.setDevice(device);
        event.setServertime(new Date());
        event.setType(deviceMoving);
//      event.setType(deviceStopped);
//      event.setPosition(position);
        return event;
    }

}
