package org.jeets.protocol.util;

import java.util.Date;

import org.jeets.protocol.Traccar;

/**
 * This class can be used to create Protobuffer messages.
 * <p>
 * Note that the methods return a Protobuffer Builder which can be modified
 * after creation and should be .build() before transmission etc.
 *
 * @author kbeigl@jeets.org
 */
public class Samples {
    // use original test members / values from JPA Samples
    public static double lat = org.jeets.model.traccar.util.Samples.lat;
    public static double lon = org.jeets.model.traccar.util.Samples.lon;
    public static String unique = org.jeets.model.traccar.util.Samples.unique;
    public static int deviceId = 999;
    // client event types
    public static Traccar.EventType 
    motionEvent = Traccar.EventType.KEY_MOTION,
    alarmEvent  = Traccar.EventType.KEY_ALARM;
    public static Traccar.AlarmType sosAlarm = Traccar.AlarmType.ALARM_SOS;

    public static Traccar.Device.Builder createDeviceWithPositionWithOneEvent() {
        Traccar.Device.Builder deviceBuilder = Traccar.Device.newBuilder();
        Traccar.Position.Builder positionBuilder = createPositionProto();
        Traccar.Event.Builder eventBuilder = createAlarmEventProto();
//      implicitly adds unmodifiable protos!
        positionBuilder.addEvent(eventBuilder);
        deviceBuilder.addPosition(positionBuilder);
        deviceBuilder.setUniqueid(unique);
        return deviceBuilder;
        // createAckForDeviceMessage();
    }

    public static Traccar.Device.Builder createDeviceProto() {
        Traccar.Device.Builder deviceBuilder = Traccar.Device.newBuilder();
        deviceBuilder.setUniqueid(unique);
        return deviceBuilder;
        // createAckForDeviceMessage();
    }

    public static Traccar.Position.Builder createPositionProto() {
        Traccar.Position.Builder positionBuilder = Traccar.Position.newBuilder();
        positionBuilder.setDevicetime(new Date().getTime()); // 2016-10-10 16:31:36 no millis ?
        positionBuilder.setFixtime(new Date().getTime());    // 2016-10-10 16:31:36
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

//  TODO: change int deviceId to String uniqueId to match Traccar Protocol purpose
    public static Traccar.Acknowledge.Builder createAckProto() {
        Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
        ackBuilder.setDeviceid(deviceId);
        return ackBuilder;
    }

}
