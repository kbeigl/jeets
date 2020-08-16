package org.jeets.protocol.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Event;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.protobuf.Jeets;
import org.jeets.protobuf.Jeets.EventType;

/**
 * Transform Traccar Entities to binary Protobuffers for sending messages from
 * client (Tracker) and transform Protos back to Java Entities on server (DCS).
 * <p>
 * The Transformer (currently) is focused on Object Relations between Device,
 * Positions and Events. Note that their relations are modeled different in
 * Persistence Unit and Protobuffer. In the end both models allow many Events
 * for one Position, i.e. n:1 The relation is unidirectional in both models -
 * only in different directions. <br>
 * Vice versa Events must have a Position to determine where and when the Event
 * took place. Therefore Events without Positions should be ignored or rather
 * shouldn't be created in the first place.
 * <p>
 * In the Protobuffer Model a Device can hold many positions and each position
 * can hold many events. Each event is accessed via the position parent.
 * <p>
 * In the Persistence Unit a Device can hold many positions and many events and
 * the event.position has to be looked up in the device.positions.
 */
public class Transformer {

//  TODO: align and cleanup main/resources/ProtobufferDeviceDecoder
//	make *Position and *Event methods private (?)

	/**
	 * Transform Device Protobuffer with related Positions and their related Events.
	 * <p>
	 * see {@link Transformer} description for relevant Object Relations.
	 * 
	 * @param deviceProto
	 * @return
	 */
    public static Device protoToEntityDevice( Jeets.Device deviceProto ) { // throws TransformException

    	Device deviceEntity = new Device();
        deviceEntity.setUniqueid(deviceProto.getUniqueid());
//      Name is only available by server lookup in admin section
//      deviceEntity.setName("DeviceTransformer");
        deviceEntity.setLastupdate(new Date());

        List<Position> positionEntities = new ArrayList<Position>();
        Set<Event> eventEntities = new HashSet<Event>();

//      TODO: assert chronological order (somewhere in the line) - LinkedHashSet ? 
//      .. and determine lastPosition for deviceEntity.setLastPosition(position);
      
        for (Jeets.Position positionProto : deviceProto.getPositionList()) {
            Position positionEntity = protoToEntityPosition(positionProto);

            positionEntity.setDevice(deviceEntity);     // n:1
            positionEntities.add(positionEntity);

//          positionProto holds eventProtos
            for (Jeets.Event eventProto : positionProto.getEventList()) {
                Event eventEntity = protoToEntityEvent(eventProto);
                eventEntity.setDevice(deviceEntity);   // n:1
//              event and position should have identical timestamp
                eventEntity.setServertime(positionEntity.getServertime());  // required !!
                eventEntity.setPosition(positionEntity); // 1:1
                eventEntities.add(eventEntity);
            }
        }

//      deviceEntity  holds eventEntities 
        deviceEntity.setEvents(eventEntities);          // 1:n
        deviceEntity.setPositions(positionEntities);    // 1:n

//      Traccar.Position lastPositionProto = 
//      deviceProto.getPosition(deviceProto.getPositionList().size());  // -1 ?

        return deviceEntity;
    }

    /**
	 * Transform Position Protobuffer to Entity without related Device or Events.
	 * <p>
	 * Relation positionEntity.setDevice(dev) is not applied and should be handled
	 * externally. <br>
	 * The Position Entity has no relation to its Events. Vice versa Event Entities
	 * are related to Device and Position Entity.<br>
	 * see {@link #protoToEntityEvent}
	 * 
	 * @param positionProto
	 * @return positionEntity
	 */
    public static Position protoToEntityPosition(Jeets.Position positionProto) {

    	Position positionEntity = new Position();
//      these don't exist in proto, set transformation time
        positionEntity.setServertime(new Date());
//      protocol protobuffer.device can/should be overridden
        positionEntity.setProtocol("pb.device");
        positionEntity.setLatitude(positionProto.getLatitude());
        positionEntity.setLongitude(positionProto.getLongitude());
        positionEntity.setAltitude(positionProto.getAltitude());
        positionEntity.setAccuracy(positionProto.getAccuracy());
        positionEntity.setCourse(positionProto.getCourse());
        positionEntity.setSpeed(positionProto.getSpeed());
        positionEntity.setValid(positionProto.getValid());
        positionEntity.setDevicetime(new Date(positionProto.getDevicetime()));
        positionEntity.setFixtime(new Date(positionProto.getFixtime()));
        
        return positionEntity;
    }

    /**
	 * Transforms Event without relation to a position, i.e. Relations
	 * eventEntity.setPosition(pos) and .setDevice(dev) are not applied and should
	 * be handled externally.
	 * 
	 * @param eventProto
	 * @return eventEntity
	 */
    /* TODO: study Traccar GTS, implement transformation of
     * Traccar.EventType.KEY_ALARM / Traccar.AlarmType.ALARM_SOS,
     * test persist and inspect database entries */
    public static Event protoToEntityEvent(Jeets.Event eventProto) {
        Event eventEntity = new Event();
        eventEntity.setType(eventProto.getEvent().name());
        return eventEntity;
    }

    /**
     * This method should handle client events on server side.
     * Server Events are not transmitted to client.
     * @param eventType
     * @return event type String
     */
    public static String eventTypeProtoToEntityString(EventType eventType) {
        switch (eventType) {
        case KEY_MOTION:
            return org.jeets.model.traccar.util.Samples.deviceMoving;
            
        case KEY_ALARM:
            return "alarmEvent";

        default:
//          TODO: improve to KEY_UNKNOWN_EVENT and add log.warn(..)
            return "unknownEvent";
        }
    }

    /**
     * Transform Device Entity to modifiable Device Builder.
     */
    public static Jeets.Device.Builder entityToProtoDevice(Device deviceEntity) {
    	
        Jeets.Device.Builder deviceBuilder = Jeets.Device.newBuilder();
        deviceBuilder.setUniqueid(deviceEntity.getUniqueid());

		List<Position> positionEntities = deviceEntity.getPositions();
		Set<Event> eventEntities = deviceEntity.getEvents();
		for (Position positionEntity : positionEntities) {
			Jeets.Position.Builder positionBuilder = entityToProtoPosition(positionEntity);

//  		nested reverse lookup: find the Event with the current positionEntity
			for (Iterator<Event> ev = eventEntities.iterator(); ev.hasNext();) {
				Event eventEntity = ev.next();
				// skip (loose!) events without position
				if ((eventEntity.getPosition() != null) 
				&& (eventEntity.getPosition().equals(positionEntity))) {
//					System.out.println("matched " + eventEntity + " with " + positionEntity);
			      	positionBuilder.addEvent(entityToProtoEvent(eventEntity));
			      	ev.remove(); // event can only have one position, speed up nested loop
//			      	don't break; since positionEntity can have more than one event
			    }
			}
//			add positionBuilder *after* above modifications; 
//			doesn't work *before* modifications via object references into deviceBuilder!
			deviceBuilder.addPosition(positionBuilder);
		}

        return deviceBuilder;
    }
    
    /**
     * Transform Position Entity to modifiable Position Builder.
     */
    public static Jeets.Position.Builder entityToProtoPosition(Position positionEntity) {
        Jeets.Position.Builder positionBuilder = Jeets.Position.newBuilder();
//      required attribute for transmission => NullPointerException !!
        positionBuilder.setDevicetime(positionEntity.getDevicetime().getTime()); // no millis ?
        positionBuilder.setFixtime(positionEntity.getFixtime().getTime()); 
        positionBuilder.setValid(positionEntity.getValid());
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
     * Transform Event Entity to modifiable Event Builder.
     */
    public static Jeets.Event.Builder entityToProtoEvent(Event eventEntity) {
        Jeets.Event.Builder eventBuilder = Jeets.Event.newBuilder();
//      the eventProto should only handle client events
//      since Protos are designed for one direction only
        eventBuilder.setEvent(stringToEventTypeProto(eventEntity.getType()));
        return eventBuilder;
    }

    /**
     * This method should handle server events and transform them to client
     * events - if available. Basically provided for development purposes.
     * 
     * @param eventString
     * @return EventType
     */
    public static EventType stringToEventTypeProto(String eventString) {

    	switch (eventString) {
        case org.jeets.model.traccar.util.Samples.deviceMoving: 
        case org.jeets.model.traccar.util.Samples.deviceStopped: 
//          TODO: special handling for Motion Event requires Motion sub/type
            return EventType.KEY_MOTION; 

        case "alarmEvent": 
//          TODO: special handling for Alarm Event requires Alarm sub/type
            return EventType.KEY_ALARM; 

        default:
//          TODO: improve to KEY_UNKNOWN_EVENT and add log.warn(..)
            return EventType.KEY_EVENT;
        }
    }

}
