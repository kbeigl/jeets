package org.jeets.model.traccar.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

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

    /**
     * Part of the subway U1 route in Hamburg, HVV Agency
     */
    public static List<Position> createU1Track() {
        List<Position> positions = new ArrayList<>();
        positions.add(createTrackPoint("04.11.17 00:37:29", 53.56985d,  10.057684d, "Wandsbeker Chaussee"));
        positions.add(createTrackPoint("04.11.17 00:39:29", 53.567647d, 10.046565d, "Ritterstraße"));
        positions.add(createTrackPoint("04.11.17 00:41:29", 53.564706d, 10.035504d, "Wartenau"));
        positions.add(createTrackPoint("04.11.17 00:42:29", 53.559529d, 10.027395d, "Lübecker Straße"));
        positions.add(createTrackPoint("04.11.17 00:43:29", 53.556626d, 10.019024d, "Lohmühlenstraße"));
        positions.add(createTrackPoint("04.11.17 00:45:29", 53.55206d,  10.009756d, "Hauptbahnhof Süd"));
        positions.add(createTrackPoint("04.11.17 00:47:29", 53.549034d, 10.006214d, "Steinstraße"));
        positions.add(createTrackPoint("04.11.17 00:48:29", 53.547669d, 10.000825d, "Meßberg"));
        positions.add(createTrackPoint("04.11.17 00:50:29", 53.552546d,  9.993471d, "Jungfernstieg"));
        positions.add(createTrackPoint("04.11.17 00:51:29", 53.558853d,  9.989303d, "Stephansplatz (Oper/CCH)"));
        positions.add(createTrackPoint("04.11.17 00:54:29", 53.572764d,  9.989055d, "Hallerstraße"));
        positions.add(createTrackPoint("04.11.17 00:56:29", 53.581794d,  9.988088d, "Klosterstern"));
        positions.add(createTrackPoint("04.11.17 00:58:29", 53.588735d,  9.990741d, "Kellinghusenstraße"));
        return positions;
    }
    
    private static Position createTrackPoint(
            String fixtime, double latitude, double longitude, String address) {
        Position pos = Samples.createPositionEntity();
        pos.setLatitude(latitude); 
        pos.setLongitude(longitude);
        pos.setAddress(address); 
        pos.setFixtime(createDate(fixtime));
        return pos;
    }
    
    private static Date createDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy hh:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static List<Device> divideU1Track(List<Position> positions) {
        List<Device> devices = new ArrayList<>();

        Device device = Samples.createDeviceEntity();
        List<Position> positionList = new ArrayList<Position>();
        positionList.add(positions.get(0));
        positionList.add(positions.get(1));
        positionList.add(positions.get(2));
        device.setPositions(positionList);
        devices.add(device);
//      ---------- GeoFence ----------
        device = Samples.createDeviceEntity();
        positionList = new ArrayList<Position>();
        positionList.add(positions.get(3));
        positionList.add(positions.get(4));
        positionList.add(positions.get(5));
        positionList.add(positions.get(6));
        positionList.add(positions.get(7));
        positionList.add(positions.get(8));
        device.setPositions(positionList);
        devices.add(device);

        device = Samples.createDeviceEntity();
        positionList = new ArrayList<Position>();
        positionList.add(positions.get(9));
        positionList.add(positions.get(10));
//      ---------- GeoFence ----------
        positionList.add(positions.get(11));
        positionList.add(positions.get(12));
        device.setPositions(positionList);
        devices.add(device);
        
        return devices;
    }

}
