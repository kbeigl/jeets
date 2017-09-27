package org.jeets.protocol;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jeets.model.traccar.jpa.Device;
import org.jeets.model.traccar.jpa.Event;
import org.jeets.model.traccar.jpa.Position;
import org.jeets.protocol.util.Samples;
import org.jeets.protocol.util.Transformer;

public class TransformerTest {

//  transform jpa.Samples (Dev, Pos, Ev) to protos and modify protos (Builder?)

    @org.junit.Test
    public void testEntityToProtoEvent() {
        Event eventEntity = org.jeets.model.traccar.util.Samples.createMovingEventEntity();
        Traccar.Event.Builder eventProto = Transformer.entityToProtoEvent(eventEntity);
        assertEquals(eventEntity.getType(), Transformer.eventTypeProtoToEntityString(eventProto.getEvent()));
        assertEquals(eventProto.getEvent(), Transformer.stringToEventTypeProto(eventEntity.getType()));
    }

    @org.junit.Test
    public void testEntityToProtoPosition() {
        Position positionEntity = org.jeets.model.traccar.util.Samples.createPositionEntity();
        Traccar.Position.Builder positionProto = Transformer.entityToProtoPosition(positionEntity);
        assertEquals(positionEntity.getLatitude(), positionProto.getLatitude(), 0.000d);
    }

    @org.junit.Test
    public void testEntityToProtoDevice() {
        Device deviceEntity = org.jeets.model.traccar.util.Samples.createDeviceEntity();
        Traccar.Device.Builder deviceProto = Transformer.entityToProtoDevice(deviceEntity);
        assertEquals(deviceProto.getUniqueid(), deviceEntity.getUniqueid());

        deviceEntity = org.jeets.model.traccar.util.Samples.createDeviceWithPositionWithTwoEvents();
        deviceProto = Transformer.entityToProtoDevice(deviceEntity);
        assertEquals(1, deviceProto.getPositionCount());
        assertEquals(Samples.lat, deviceProto.getPosition(0).getLatitude(), 0.000d);
//      Note that Events are not transformed, 
//        since Entities apply positionID and not Position Entity:
        assertEquals(0, deviceProto.getPosition(0).getEventCount());
//      Events can be transformed individually and related in the program context
//      deviceEntity.getEvents() ...
//      Transformer.entityToProtoEvent(eventEntity)
}

//  transform protocol.Samples (Dev, Pos, Ev) to Entities

    @org.junit.Test
    public void testProtoToEntityEvent() {
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

    @org.junit.Test
    public void testProtoToEntityPosition() {
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
            for (Traccar.Event event : protoEvents) {
                Event eventEntity = Transformer.protoToEntityEvent(eventBuilder.build());
                assertEquals(Samples.alarmEvent.name(), eventEntity.getType());
            }
        }
    }

    @org.junit.Test
    public void testProtoToEntityDevice() {
        Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithPositionWithOneEvent();
        assertEquals(1, deviceBuilder.getPositionCount());
        assertEquals(Samples.lat, deviceBuilder.getPosition(0).getLatitude(), 0.000d);
        assertEquals(1, deviceBuilder.getPosition(0).getEventCount());
        assertEquals(Traccar.EventType.KEY_ALARM, deviceBuilder.getPosition(0).getEvent(0).getEvent());
        
        Device deviceEntity = Transformer.protoToEntityDevice(deviceBuilder.build());
        assertEquals(1, deviceEntity.getEvents().size());
        assertEquals(1, deviceEntity.getPositions().size());
//      .. deviceEntity.getPositionid() .. !
    }

}
