package org.jeets.protocol.util;

import java.util.Date;

import org.jeets.protobuf.Traccar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * These samples demonstrate how to create Protobuffer messages and can be used
 * for development and testing.
 * <p>
 * Note that the methods return a Protobuffer Builder which can be modified
 * after creation and should be .build() before transmission etc.
 *
 * @author kbeigl@jeets.org
 */
public class Samples {
	
	private static final Logger log = LoggerFactory.getLogger(Samples.class);
    // use original test members / values from PU Samples
    public static double lat = org.jeets.model.traccar.util.Samples.lat;
    public static double lon = org.jeets.model.traccar.util.Samples.lon;
    public static String uniqueId = org.jeets.model.traccar.util.Samples.uniqueId;
	public static int deviceId = 999; // rethink
    // client event types
    public static Traccar.EventType alarmEvent = Traccar.EventType.KEY_ALARM;
    public static Traccar.AlarmType sosAlarm = Traccar.AlarmType.ALARM_SOS;
    public static Traccar.EventType motionEvent = Traccar.EventType.KEY_MOTION;
//  TODO: model in proto file, align with pu Samples and Traccar:
//  TYPE_DEVICE_MOVING = ;
//  TYPE_DEVICE_STOPPED = ;
    
    /**
     * compare pu-traccar Samples: <br>
     * createDeviceWithTwoPositionsWithEvent()
     */
    public static Traccar.Device.Builder createDeviceWithTwoPositionsWithEvent() {

    	Traccar.Device.Builder deviceBuilder = createDeviceWithTwoPositions();
    	deviceBuilder.getPositionBuilder(0).addEvent(createAlarmEventProto());
    	deviceBuilder.getPositionBuilder(1).addEvent(createMotionEventProto());

//    	log.debug("created Protobuffer:\n{}", deviceBuilder);
        return deviceBuilder;
    }

    /**
     * compare pu-traccar Samples: <br>
     * createDeviceWithTwoPositions()
     */
    public static Traccar.Device.Builder createDeviceWithTwoPositions() {

    	Traccar.Device.Builder deviceBuilder = Traccar.Device.newBuilder();
        deviceBuilder.setUniqueid(uniqueId);

        Traccar.Position.Builder positionBuilder = createPositionProto();
        deviceBuilder.addPosition(positionBuilder);
        positionBuilder = createPositionProto();
//      *8* Obere Regenstraße, Regensburg, BY, DE
        positionBuilder.setLatitude(49.03107129d);
        positionBuilder.setLongitude(12.10331786d);

		deviceBuilder.addPosition(positionBuilder);
//    	log.debug("created Protobuffer:\n{}", deviceBuilder);
        return deviceBuilder;
    }

    /**
     * compare pu-traccar Samples: <br>
     * createDeviceWithPositionWithTwoEvents()
     */
    public static Traccar.Device.Builder createDeviceWithPositionWithTwoEvents() {

    	Traccar.Device.Builder deviceBuilder = Traccar.Device.newBuilder();
        Traccar.Position.Builder positionBuilder = createPositionProto();

        Traccar.Event.Builder eventBuilder = createAlarmEventProto();
        positionBuilder.addEvent(eventBuilder);
        eventBuilder = createMotionEventProto();
        positionBuilder.addEvent(eventBuilder);
        
        deviceBuilder.addPosition(positionBuilder);
        deviceBuilder.setUniqueid(uniqueId);
    	log.debug("created Protobuffer:\n{}", deviceBuilder);
        return deviceBuilder;
        // createAckForDeviceMessage();
    }

    public static Traccar.Device.Builder createDeviceWithPositionWithOneEvent() {

    	Traccar.Device.Builder deviceBuilder = Traccar.Device.newBuilder();
        Traccar.Position.Builder positionBuilder = createPositionProto();
        Traccar.Event.Builder eventBuilder = createAlarmEventProto();
        
//      implicitly adds unmodifiable protos!
        positionBuilder.addEvent(eventBuilder);
        deviceBuilder.addPosition(positionBuilder);
        deviceBuilder.setUniqueid(uniqueId);
        
        return deviceBuilder;
    }

    /**
	 * Create Device Proto Builder with minimum attributes and without Position,
	 * which can be added externally.<br>
	 * Note the binary Device Proto is not .build() and can still be modified
	 * externally.
	 */
    public static Traccar.Device.Builder createDeviceProto() {
        Traccar.Device.Builder deviceBuilder = Traccar.Device.newBuilder();
        deviceBuilder.setUniqueid(uniqueId);
        return deviceBuilder;
        // createAckForDeviceMessage();
    }

    /**
	 * Create Position Proto with minimum attributes and without Event, which can be
	 * added and attached to Device externally.
	 */
    public static Traccar.Position.Builder createPositionProto() {
        Traccar.Position.Builder positionBuilder = Traccar.Position.newBuilder();
     // 2016-10-10 16:31:36 verify millis for device- and fixtime!
        positionBuilder.setDevicetime(new Date().getTime());
        positionBuilder.setFixtime(new Date().getTime());
        positionBuilder.setValid(true);
        positionBuilder.setLatitude(lat);
        positionBuilder.setLongitude(lon);
        positionBuilder.setAltitude(333.111d);
        positionBuilder.setAccuracy(0.345d);
        positionBuilder.setSpeed(12.345d);
        positionBuilder.setCourse(100.123d);
        // positionBuilder.setAttributes(position.getAttributes());
        return positionBuilder;
    }

    public static Traccar.Event.Builder createAlarmEventProto() {
        Traccar.Event.Builder eventBuilder = Traccar.Event.newBuilder();
        eventBuilder.setEvent(alarmEvent);
        eventBuilder.setAlarm(sosAlarm);
        return eventBuilder;
    }

    public static Traccar.Event.Builder createMotionEventProto() {
        Traccar.Event.Builder eventBuilder = Traccar.Event.newBuilder();
        eventBuilder.setEvent(motionEvent);
        return eventBuilder;
    }

//  TODO: change int deviceId to String uniqueId to serve Traccar Protocol purpose
    public static Traccar.Acknowledge.Builder createAckProto() {
        Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
        ackBuilder.setDeviceid(deviceId);
        return ackBuilder;
    }

}
