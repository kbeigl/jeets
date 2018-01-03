package org.jeets.protocol.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Event;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.Traccar.EventType;

public class Transformer {

//  TODO: align and cleanup main/resources/ProtobufferDeviceDecoder
//      .transfromPositionProtoToEntity 
//  <-> .transformDeviceProtoToPositionEntities

    public static Device protoToEntityDevice( Traccar.Device deviceProto ) { // throws TransformException
        Device deviceEntity = new Device();
        deviceEntity.setUniqueid(deviceProto.getUniqueid());
//      Name is only available by server lookup in admin section
//      deviceEntity.setName("DeviceTransformer");
        deviceEntity.setLastupdate(new Date());

        List<Position> positionEntities = new ArrayList<Position>();
        Set<Event> eventEntities = new HashSet<Event>();

//      TODO: ensure chronological order (somewhere in the line) - LinkedHashSet ? 
//      .. and determine lastPosition for deviceEntity.setPositionid(positionid);
      
        List<Traccar.Position> positionProtos = deviceProto.getPositionList();
        for (Traccar.Position positionProto : positionProtos) {
            Position positionEntity = protoToEntityPosition(positionProto);
            positionEntity.setDevice(deviceEntity);     // n:1
            positionEntities.add(positionEntity);

            List<Traccar.Event> eventProtos = positionProto.getEventList();
            for (Traccar.Event eventProto : eventProtos) {
                Event eventEntity = protoToEntityEvent(eventProto);
                eventEntity.setDevices(deviceEntity);   // n:1
//              event and position should have identical timestamp
                eventEntity.setServertime(positionEntity.getServertime());  // required !!
                eventEntities.add(eventEntity);
            }
        }

        deviceEntity.setEvents(eventEntities);          // 1:n
        deviceEntity.setPositions(positionEntities);    // 1:n

//      Traccar.Position lastPositionProto = 
//      deviceProto.getPosition(deviceProto.getPositionList().size());  // -1 ?

        return deviceEntity;
    }

    /**
     * Transform Position Protobuffer to Entity with related Events. The
     * Position Entity has no relation to its Events and is currently internally
     * modeled via Device
     */
    public static Position protoToEntityPosition(Traccar.Position positionProto) {
        Position positionEntity = new Position();
//      these doesn't exist in proto, set transformation time
        positionEntity.setServertime(new Date());
//      generalize and collect (somewhere)
        positionEntity.setProtocol("pb.device");
        positionEntity.setLatitude(positionProto.getLatitude());
        positionEntity.setLongitude(positionProto.getLongitude());
        positionEntity.setAltitude(positionProto.getAltitude());
        positionEntity.setAccuracy(positionProto.getAccuracy());
        positionEntity.setCourse(positionProto.getCourse());
        positionEntity.setSpeed(positionProto.getSpeed());
//      fix proto file to transfer valid flag (wrong description in book!)
        positionEntity.setValid(true);
        positionEntity.setDevicetime(new Date(positionProto.getDevicetime()));
        positionEntity.setFixtime(new Date(positionProto.getFixtime()));

//        List<Traccar.Event> positionEvents = positionProto.getEventList();
//        for (Traccar.Event eventProto : positionEvents) {
//            Event eventEntity = protoToEntityEvent(eventProto);
////          TODO: change parameter to (Position) Entity, avoid DB-ID
////          eventEntity.setPositionid(positionid);
//        }
        
        return positionEntity;
    }

    /* TODO: study traccar GTS and determine transformation of 
             Traccar.EventType.KEY_ALARM / Traccar.AlarmType.ALARM_SOS */
    public static Event protoToEntityEvent(Traccar.Event eventProto) {
        Event eventEntity = new Event();
        eventEntity.setType(eventProto.getEvent().name());
        return eventEntity;
    }

    /**
     * Transform Event Entity to modifiable Event Builder.
     */
    public static Traccar.Event.Builder entityToProtoEvent(Event eventEntity) {
        Traccar.Event.Builder eventBuilder = Traccar.Event.newBuilder();
//      the eventProto should only handle client events
//      since Protos are designed for one direction only
        eventBuilder.setEvent(stringToEventTypeProto(eventEntity.getType()));
        return eventBuilder;
    }
    
//  currently not used 
    public void entitiesToProtoPositions(List<Position> positionEntities) {
        List<Traccar.Position.Builder> protoPositionBuilders = new ArrayList<>();
        for (Position positionEntity : positionEntities) {
            protoPositionBuilders.add(entityToProtoPosition(positionEntity));
        }
    }

    /**
     * Transform Position Entity to modifiable Position Builder.
     */
    public static Traccar.Position.Builder entityToProtoPosition(Position positionEntity) {
        Traccar.Position.Builder positionBuilder = Traccar.Position.newBuilder();
        // see java8.timeapi package !!
//      required attribute for transmission => NullPointerException !!
        positionBuilder.setDevicetime(positionEntity.getDevicetime().getTime()); // no millis ?
        positionBuilder.setFixtime(positionEntity.getFixtime().getTime()); 
        positionBuilder.setValid(positionEntity.isValid());
        positionBuilder.setLatitude(positionEntity.getLatitude());
        positionBuilder.setLongitude(positionEntity.getLongitude());
        positionBuilder.setAltitude(positionEntity.getAltitude());
        positionBuilder.setAccuracy(positionEntity.getAccuracy());
        positionBuilder.setSpeed(positionEntity.getSpeed());
        positionBuilder.setCourse(positionEntity.getCourse());
//      the originating Device is not part of the positionBuilder!
//      positionBuilder.setAttributes(positionEntity.getAttributes());
        
//      Event Entity is not provided by Position Entity !?
//      positionBuilder.addEvent(createProtoEvent());

        return positionBuilder;
    }

    /**
     * Transform Device Entity to modifiable Device Builder.
     * <p>
     * Note that Events are not transformed, since Entities apply positionID and
     * not Position Entity.
     */
    public static Traccar.Device.Builder entityToProtoDevice(Device deviceEntity) {
        Traccar.Device.Builder deviceBuilder = Traccar.Device.newBuilder();
        deviceBuilder.setUniqueid(deviceEntity.getUniqueid());

        List<Position> positionEntities = deviceEntity.getPositions();
//      Set<Event> eventEntities = deviceEntity.getEvents();
        for (Position positionEntity : positionEntities) {
            Traccar.Position.Builder positionBuilder = entityToProtoPosition(positionEntity);
//            for (Event eventEntity : eventEntities) {
////              can only be set via database-ID lookup!
//                eventEntity.getPositionid();
//                positionBuilder.addEvent(entityToProtoEvent(eventEntity));
//            }
            deviceBuilder.addPosition(positionBuilder);
        }

        return deviceBuilder;
    }
    
//  lookup relations

    /**
     * This method should handle client events on server side.
     * Server Events don't need to be transmitted to client.
     * @param eventType
     * @return
     */
    public static String eventTypeProtoToEntityString(EventType eventType) {
        switch (eventType) {
        case KEY_MOTION:
            return org.jeets.model.traccar.util.Samples.deviceMoving; 

        default:
//          TODO: improve to KEY_UNKNOWN_EVENT and add log.warn(..)
            return "unknownEvent";
        }
    }

    /**
     * This method should handle server events and transform them to client
     * events - if available. It is basically provided for development purposes.
     * 
     * @param eventType
     * @return
     */
    public static EventType stringToEventTypeProto(String eventString) {
//      TODO: special handling for Alarm Event requires Alarm Type 
        switch (eventString) {
        case org.jeets.model.traccar.util.Samples.deviceMoving: 
            return EventType.KEY_MOTION; 

        default:
//          TODO: improve to KEY_UNKNOWN_EVENT and add log.warn(..)
            return EventType.KEY_EVENT;
        }
    }

}
