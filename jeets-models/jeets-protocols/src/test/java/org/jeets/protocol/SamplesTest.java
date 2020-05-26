package org.jeets.protocol;

import static org.junit.Assert.*;

import org.jeets.protobuf.Traccar;
import org.jeets.protocol.util.Samples;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create, modify and build Traccar Protocol Messages.
 * 
 * @author kbeigl@jeets.org
 */
public class SamplesTest {

	private static final Logger log = LoggerFactory.getLogger(SamplesTest.class);

	@Test
    public void buildAckMessage() {
		log.info("buildAckMessage device(unique{}) ...", 888);
        
    	Traccar.Acknowledge.Builder ackBuilder = Samples.createAckProto();
        assertEquals(Samples.deviceId, ackBuilder.getDeviceid());
        ackBuilder.setDeviceid(888);
        
//      create final unmodifiable binary message 
        Traccar.Acknowledge ackProto = ackBuilder.build();
        assertEquals(888, ackProto.getDeviceid());
        
//      modifies builder message
        ackBuilder.setDeviceid(999);
//      transmit again?
        ackProto = ackBuilder.build();
        assertEquals(999, ackProto.getDeviceid());
    }

    @Test
    public void buildEventMessage() {
        
    	Traccar.Event.Builder eventBuilder = Samples.createAlarmEventProto();
        assertEquals(Samples.alarmEvent, eventBuilder.getEvent());
        assertEquals(Samples.sosAlarm, eventBuilder.getAlarm());

        eventBuilder.setEvent(Samples.motionEvent);
//      eventBuilder.setAlarm(null);    // doesn't work :(
        
        Traccar.Event eventProto = eventBuilder.build();
        assertEquals(Samples.motionEvent, eventProto.getEvent());
    }

    @Test
    public void buildPositionMessage() {
        
    	Traccar.Position.Builder positionBuilder = Samples.createPositionProto();
        assertEquals(Samples.lat, positionBuilder.getLatitude(), 0.00001);
        assertEquals(Samples.lon, positionBuilder.getLongitude(), 0.00001);

        double newLatitude = 48.123d;
        positionBuilder.setLatitude(newLatitude);

        Traccar.Position positionProto = positionBuilder.build();
        assertEquals(newLatitude, positionProto.getLatitude(), 0.00001);
    }

    @Test
    public void buildDeviceMessage() {
        
    	Traccar.Device.Builder deviceBuilder = Samples.createDeviceProto();
        assertEquals(Samples.uniqueId, deviceBuilder.getUniqueid());
        
        String uniqueId = "TestDevice";
        deviceBuilder.setUniqueid(uniqueId);

        Traccar.Device deviceProto = deviceBuilder.build();
        assertEquals(uniqueId, deviceProto.getUniqueid());
    }
    
    /**
     * Single Device Message including Position Message with Event Message.
     */
    @Test
    public void buildDeviceWithPositionWithTwoEvents() {

    	Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithPositionWithTwoEvents();
//    	Traccar.Device.Builder deviceBuilder = Samples.createDeviceWithPositionWithOneEvent();
        
//      deviceBuilder can be modified
        String uniqueId = "TestDevice";
        deviceBuilder.setUniqueid(uniqueId);
        assertTrue(deviceBuilder.getUniqueid().equals(uniqueId));
        
//      add and remove
        deviceBuilder.addPosition(Samples.createPositionProto());
        assertTrue(deviceBuilder.getPositionCount()==2);
        deviceBuilder.removePosition(1);
        assertTrue(deviceBuilder.getPositionCount()==1);

        assertTrue(deviceBuilder.getPosition(0).getEventCount()==2);
        
        Traccar.Device deviceProto = deviceBuilder.build();
        assertEquals(uniqueId, deviceProto.getUniqueid());
    }
    
//  TODO:
//  buildDeviceWithPositionWithOneEvent() 
//  buildDeviceWithTwoPositions()
//  buildDeviceWithTwoPositionsWithEvent()

}
