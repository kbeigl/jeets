package org.jeets.protocol;

import static org.junit.Assert.*;

import org.jeets.protocol.Traccar;
import org.jeets.protocol.util.Samples;

/**
 * Various Tests for the Traccar Protocol Messages.
 * 
 * @author kbeigl@jeets.org
 */
public class TraccarTest {

    @org.junit.Test
    public void testAckMessage() {
        Traccar.Acknowledge.Builder ackBuilder = Samples.createAckProto();
        assertEquals(Samples.deviceId, ackBuilder.getDeviceid());
        ackBuilder.setDeviceid(888);
        Traccar.Acknowledge ackProto = ackBuilder.build();
        assertEquals(888, ackProto.getDeviceid());
    }

    /**
     * Test single Event Message.
     */
    @org.junit.Test
    public void testEventMessage() {
        Traccar.Event.Builder eventBuilder = Samples.createAlarmEventProto();
        assertEquals(Samples.alarmEvent, eventBuilder.getEvent());
        assertEquals(Samples.sosAlarm, eventBuilder.getAlarm());
        eventBuilder.setEvent(Samples.motionEvent);
//      eventBuilder.setAlarm(null);    // doesn't work :(
        Traccar.Event eventProto = eventBuilder.build();
        assertEquals(Samples.motionEvent, eventProto.getEvent());
    }

    /**
     * Test single Position Message.
     */
    @org.junit.Test
    public void testPositionMessage() {
        Traccar.Position.Builder positionBuilder = Samples.createPositionProto();
        assertEquals(Samples.lat, positionBuilder.getLatitude(), 0.00001);
        assertEquals(Samples.lon, positionBuilder.getLongitude(), 0.00001);
        double newLatitude = 48.123d;
        positionBuilder.setLatitude(newLatitude);
        Traccar.Position positionProto = positionBuilder.build();
        assertEquals(newLatitude, positionProto.getLatitude(), 0.00001);
    }

    /**
     * Test single Device Message.
     */
    @org.junit.Test
    public void testDeviceMessage() {
        Traccar.Device.Builder deviceBuilder = Samples.createDeviceProto();
        assertEquals(Samples.unique, deviceBuilder.getUniqueid());
        String uniqueId = "TestDevice";
        deviceBuilder.setUniqueid(uniqueId);
        Traccar.Device deviceProto = deviceBuilder.build();
        assertEquals(uniqueId, deviceProto.getUniqueid());
    }
    
    /**
     * Test single Device Message including Position Message with Event Message.
     */
    @org.junit.Test
    public void createDeviceWithPositionWithTwoEvents() {
        Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithPositionWithOneEvent();
        assertTrue(deviceBuilder.getPositionCount()==1);
//      deviceBuilder can be modified
        String uniqueId = "TestDevice";
        deviceBuilder.setUniqueid(uniqueId);
        assertTrue(deviceBuilder.getUniqueid().equals(uniqueId));
//      position and event can not be modified as they were implicitly built
//      Traccar.Position.Builder positionBuilder = deviceBuilder.getPosition(0);
//      but they can be added (with new Events), replaced or removed 
        deviceBuilder.addPosition(Samples.createPositionProto());
        assertTrue(deviceBuilder.getPositionCount()==2);
        deviceBuilder.removePosition(1);
        assertTrue(deviceBuilder.getPositionCount()==1);
        assertTrue(deviceBuilder.getPosition(0).getEventCount()==1);
        Traccar.Device deviceProto = deviceBuilder.build();
        assertEquals(uniqueId, deviceProto.getUniqueid());
    }

//  createAckForDeviceMessage();
//  testAckMessage ..

}
