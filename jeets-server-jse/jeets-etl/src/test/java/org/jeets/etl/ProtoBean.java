package org.jeets.etl;

import java.util.Date;

import org.apache.camel.Handler;
import org.jeets.protocol.Traccar;
import org.jeets.protocol.Traccar.AlarmType;
import org.jeets.protocol.Traccar.Device;
import org.jeets.protocol.Traccar.Event;
import org.jeets.protocol.Traccar.EventType;
import org.jeets.protocol.Traccar.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Register ProtoBean to simulate incoming Device Protobuffer message or simply
 * use it to create the message.
 */
public class ProtoBean {
//  class is not needed in main (any more)
//  -> move to util package or test environment
    private static final Logger LOG = LoggerFactory.getLogger(ProtoBean.class);

    double lat = 49.99d, lon = 12.11d;
    String unique = "11";
    EventType alarmEvent = EventType.KEY_ALARM;
    AlarmType sosAlarm = AlarmType.ALARM_SOS;

    @Handler
    public Traccar.Device createProtoDevice() {
        Traccar.Device.Builder deviceBuilder = Device.newBuilder();
        deviceBuilder.setUniqueid(unique);
        deviceBuilder.addPosition(createProtoPosition());
        LOG.info("created DeviceProto: {}", deviceBuilder);
//      for testing it would be better to return the builder for modifications!
        return deviceBuilder.build();
//      createAckForDeviceMessage();
    }

    private Traccar.Position createProtoPosition() {
        Traccar.Position.Builder positionBuilder = Position.newBuilder();
        positionBuilder.setDevicetime(new Date().getTime());  // 2016-10-10 16:31:36 no millis ?
        positionBuilder.setFixtime(new Date().getTime());     // 2016-10-10 16:31:36
        positionBuilder.setValid(true);
        positionBuilder.setLatitude(lat);
        positionBuilder.setLongitude(lon);
        positionBuilder.setAltitude(333.111d);
        positionBuilder.setAccuracy(0.345d);
        positionBuilder.setSpeed(12.345d);
        positionBuilder.setCourse(100.123d);
//      positionBuilder.setAttributes(position.getAttributes());
//      positionBuilder.addEvent(createProtoEvent());
        return positionBuilder.build();
    }
    
    private Traccar.Event createProtoEvent() {
        Traccar.Event.Builder eventBuilder = Event.newBuilder();
        eventBuilder.setEvent(alarmEvent);
        eventBuilder.setAlarm(sosAlarm);
        return eventBuilder.build();
    }
}
