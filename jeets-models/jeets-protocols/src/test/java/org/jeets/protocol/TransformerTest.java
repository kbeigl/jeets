package org.jeets.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.Set;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Event;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.protocol.util.Samples;
import org.jeets.protocol.util.Transformer;
import org.junit.Test;

public class TransformerTest {

//  transform JPA.Samples (Dev, Pos, Ev) to protos and modify protos (Builder?)

    @Test
    public void transformEntityToProtoEvent() {
        
    	Event eventEntity = org.jeets.model.traccar.util.Samples.createMovingEventEntity();
        Traccar.Event.Builder eventBuilder = Transformer.entityToProtoEvent(eventEntity);
		assertEquals(eventEntity.getType(), Transformer.eventTypeProtoToEntityString(eventBuilder.getEvent()));
        assertEquals(eventBuilder.getEvent(), Transformer.stringToEventTypeProto(eventEntity.getType()));

        Traccar.Event eventProto = eventBuilder.build();
        assertEquals(eventEntity.getType(), Transformer.eventTypeProtoToEntityString(eventProto.getEvent()));
        assertEquals(eventProto.getEvent(), Transformer.stringToEventTypeProto(eventEntity.getType()));
    }

    @Test
    public void transformEntityToProtoPosition() {
 
    	Position positionEntity = org.jeets.model.traccar.util.Samples.createPositionEntity();
        Traccar.Position.Builder positionBuilder = Transformer.entityToProtoPosition(positionEntity);
        assertEquals(positionEntity.getLatitude(), positionBuilder.getLatitude(), 0.000d);

        Traccar.Position positionProto = positionBuilder.build();
        assertEquals(positionEntity.getLatitude(), positionProto.getLatitude(), 0.000d);
    }

    @Test
    public void transformEntityToProtoDevice() {
        
    	Device deviceEntity = org.jeets.model.traccar.util.Samples.createDeviceEntity();
        Traccar.Device.Builder deviceBuilder = Transformer.entityToProtoDevice(deviceEntity);
        assertEquals(deviceBuilder.getUniqueid(), deviceEntity.getUniqueid());

        Traccar.Device deviceProto = deviceBuilder.build();
        assertEquals(deviceProto.getUniqueid(), deviceEntity.getUniqueid());
    }

    @Test
    public void transformEntityDeviceWithTwoPositionsWithEvent() {
//	    implies transformEntityDeviceWithTwoPositions() {
    	
    	Device deviceEntity = org.jeets.model.traccar.util.Samples.createDeviceWithTwoPositionsWithEvent();
//		System.out.println("deviceEntity (" + deviceEntity.getPositions().size() + " pos, "
//			+ deviceEntity.getEvents().size() + " evs) ");
        
		Traccar.Device.Builder deviceBuilder = Transformer.entityToProtoDevice(deviceEntity);
//		System.out.println("deviceBuilder (" + deviceBuilder.getPositionCount() + " pos, "
//			+ deviceBuilder.getPosition(0).getEventCount() + " evs)\n" + deviceBuilder);

		assertEquals(2, deviceBuilder.getPositionCount());
        assertEquals(Samples.lat, deviceBuilder.getPosition(0).getLatitude(), 0.000d);
        assertEquals(49.03107129d, deviceBuilder.getPosition(1).getLatitude(), 0.000d);
        assertEquals(1, deviceBuilder.getPosition(0).getEventCount());
        assertEquals(1, deviceBuilder.getPosition(1).getEventCount());

        Traccar.Device deviceProto = deviceBuilder.build();
		assertEquals(2, deviceProto.getPositionCount());
        assertEquals(Samples.lat, deviceProto.getPosition(0).getLatitude(), 0.000d);
        assertEquals(49.03107129d, deviceProto.getPosition(1).getLatitude(), 0.000d);
        assertEquals(1, deviceProto.getPosition(0).getEventCount());
        assertEquals(1, deviceProto.getPosition(1).getEventCount());
    }

    @Test
    public void transformEntityDeviceWithPositionWithTwoEvents() {

    	Device deviceEntity = org.jeets.model.traccar.util.Samples.createDeviceWithPositionWithTwoEvents();
//		System.out.println("deviceEntity (" + deviceEntity.getPositions().size() + " pos, "
//  		+ deviceEntity.getEvents().size() + " evs) ");
        
		Traccar.Device.Builder deviceBuilder = Transformer.entityToProtoDevice(deviceEntity);
//		System.out.println("deviceBuilder (" + deviceBuilder.getPositionCount() + " pos, "
//			+ deviceBuilder.getPosition(0).getEventCount() + " evs)\n" + deviceBuilder);

		assertEquals(1, deviceBuilder.getPositionCount());
        assertEquals(Samples.lat, deviceBuilder.getPosition(0).getLatitude(), 0.000d);
        assertEquals(2, deviceBuilder.getPosition(0).getEventCount());
//      System.out.println("device.getPosition(0): " + deviceBuilder.getPosition(0));

        Traccar.Device deviceProto = deviceBuilder.build();
        assertEquals(1, deviceProto.getPositionCount());
        assertEquals(Samples.lat, deviceProto.getPosition(0).getLatitude(), 0.000d);
        assertEquals(2, deviceProto.getPosition(0).getEventCount());
    }

//  transform PROTOCOL.Samples (Dev, Pos, Ev) to Entities

    @Test
    public void transformProtoToEntityEvent() {
    	
        Traccar.Event.Builder eventBuilder = Samples.createAlarmEventProto();
        assertEquals(Samples.alarmEvent, eventBuilder.getEvent());
        assertEquals(Samples.sosAlarm, eventBuilder.getAlarm());
        
        Event eventEntity = Transformer.protoToEntityEvent(eventBuilder.build());
        assertEquals(Samples.alarmEvent.name(), eventEntity.getType());
//      eventBuilder can still be modified
        eventBuilder.setEvent(Samples.motionEvent);
//      eventBuilder.setAlarm(null);    // doesn't work :(

        eventEntity = Transformer.protoToEntityEvent(eventBuilder.build());
        assertEquals(Samples.motionEvent.name(), eventEntity.getType());
    }

    @Test
    public void transformProtoToEntityPosition() {
    	
        Traccar.Position.Builder positionBuilder = Samples.createPositionProto();
        assertEquals(Samples.lat, positionBuilder.getLatitude(), 0.000d);
        assertEquals(Samples.lon, positionBuilder.getLongitude(), 0.000d);

        Traccar.Event.Builder eventBuilder = Samples.createAlarmEventProto();
        positionBuilder.addEvent(eventBuilder);
        assertEquals(1, positionBuilder.getEventCount());
        assertEquals(Traccar.EventType.KEY_ALARM, positionBuilder.getEvent(0).getEvent());
        
        Position positionEntity = Transformer.protoToEntityPosition(positionBuilder.build());
        assertEquals(Samples.lat, positionEntity.getLatitude(), 0.000d);
        assertEquals(Samples.lon, positionEntity.getLongitude(), 0.000d);

//      The Position Entity has no relation to its Events
//      Therefore Events have to be converted (and related) explicitly
        if (positionBuilder.getEventCount() > 0) {
            List<Traccar.Event> protoEvents = positionBuilder.getEventList();
            for (Traccar.Event event : protoEvents) {	// ?event not used?
                Event eventEntity = Transformer.protoToEntityEvent(eventBuilder.build());
                assertEquals(Samples.alarmEvent.name(), eventEntity.getType());
            }
        }
    }

    @Test
    public void transformProtoDeviceWithPositionWithOneEvent() {
    	
        Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithPositionWithOneEvent();
        assertEquals(1, deviceBuilder.getPositionCount());
        assertEquals(Samples.lat, deviceBuilder.getPosition(0).getLatitude(), 0.000d);
        assertEquals(1, deviceBuilder.getPosition(0).getEventCount());
        assertEquals(Traccar.EventType.KEY_ALARM, deviceBuilder.getPosition(0).getEvent(0).getEvent());
        
        Device deviceEntity = Transformer.protoToEntityDevice(deviceBuilder.build());
        assertEquals(1, deviceEntity.getEvents().size());
        assertEquals(1, deviceEntity.getPositions().size());
//      .. deviceEntity.getLastPosition() .. !
    }

    @Test
    public void transformProtoDeviceWithPositionWithTwoEvents() {
    	
        Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithPositionWithTwoEvents();
        assertEquals(1, deviceBuilder.getPositionCount());
        
        Traccar.Position.Builder firstPosBuilder = deviceBuilder.getPositionBuilder(0);
        assertEquals(Samples.lat, firstPosBuilder.getLatitude(), 0.000d);
        assertEquals(2, firstPosBuilder.getEventCount());
        assertEquals(Traccar.EventType.KEY_ALARM, firstPosBuilder.getEvent(0).getEvent());
        assertEquals(Traccar.EventType.KEY_MOTION, firstPosBuilder.getEvent(1).getEvent());
        
        Traccar.Device deviceProto = deviceBuilder.build();
        assertEquals(1, deviceProto.getPositionCount());
        
        Traccar.Position firstPosition = deviceProto.getPosition(0);
        assertEquals(Samples.lat, firstPosition.getLatitude(), 0.000d);
        assertEquals(2, firstPosition.getEventCount());
        assertEquals(Traccar.EventType.KEY_ALARM, firstPosition.getEvent(0).getEvent());
        assertEquals(Traccar.EventType.KEY_MOTION, firstPosition.getEvent(1).getEvent());
        
        Device deviceEntity = Transformer.protoToEntityDevice(deviceProto);
        Set<Event> eventEntites = deviceEntity.getEvents();
        assertEquals(2, eventEntites.size());
        assertEquals(1, deviceEntity.getPositions().size());
        Event[] eventArray = eventEntites.toArray(new Event[eventEntites.size()]);
        for (int ev = 0; ev < eventArray.length; ev++) {
//    		System.out.println("event: " + eventArray[ev]);
    		assertSame("Event " + eventArray[ev].getType() + " has different Position Object than device!",
    				deviceEntity.getPositions().get(0), eventArray[ev].getPosition());
		}
    }

    @Test
    public void transformProtoDeviceWithTwoPositionsWithEvent() {
    	
        Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithTwoPositionsWithEvent();
        assertEquals(2, deviceBuilder.getPositionCount());
        assertEquals(Samples.lat, deviceBuilder.getPosition(0).getLatitude(), 0.000d);
        assertEquals(1, deviceBuilder.getPosition(0).getEventCount());
        assertEquals(Traccar.EventType.KEY_ALARM, deviceBuilder.getPosition(0).getEvent(0).getEvent());
        assertEquals(Traccar.EventType.KEY_MOTION, deviceBuilder.getPosition(1).getEvent(0).getEvent());
        
        Device deviceEntity = Transformer.protoToEntityDevice(deviceBuilder.build());
        assertEquals(2, deviceEntity.getEvents().size());
        assertEquals(2, deviceEntity.getPositions().size());
//      .. deviceEntity.getLastPosition() .. !
//      System.out.println("to "+ deviceEntity);
    }

}
