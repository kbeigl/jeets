/*
 * Copyright 2012 - 2016 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.protocol;

import org.jboss.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.model.Position;

import org.jeets.protocol.Traccar;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Protobuffer Decoder from Device messages
 * with Positions and Events to Traccar Entities.
 *
 * @author Kristof Beiglböck kbeigl@jeets.org
 */
public class ProtobufferDeviceDecoder extends BaseProtocolDecoder {

    public ProtobufferDeviceDecoder(ProtobufferDeviceProtocol protocol) {
        super(protocol);
    }

    @Override
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Traccar.Device protoDevice = (Traccar.Device) msg; // extract protobuffer
        // traccar Device does not implement .toString()
        System.out.println("received device:\n" + protoDevice.toString());

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, protoDevice.getUniqueid());
        if (deviceSession == null) {
            return null;
        }
        long deviceId = deviceSession.getDeviceId();

        List<Position> positions = transformDeviceProtoToPositionEntities(protoDevice, deviceId);

        // send ACK to tracker (which is waiting for response to shutdown connection!)
        Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
        ackBuilder.setDeviceid(123);
        channel.write(ackBuilder.build());
        System.out.println("responded with ack:\n" + ackBuilder.toString());

        if (positions.size() < 1) {
            return null;
        }
        if (positions.size() == 1) {
            return positions.get(0);
        } else {
            return positions;
        }
    }

    /**
     * Create List of Position Entities for internal handling in traccar.
     * @param protoDevice
     * @param deviceId
     * @return entityPositions
     */
    private List<Position> transformDeviceProtoToPositionEntities(Traccar.Device protoDevice, long deviceId) {
        List<Position> entityPositions = new ArrayList<Position>();
        for (int pos = 0; pos < protoDevice.getPositionCount(); pos++) {
            Position entityPosition = transfromPositionProtoToEntity(protoDevice.getPosition(pos), deviceId);
            entityPositions.add(entityPosition);
        }
        return entityPositions;
    }

    /**
     * Map received Protobuffer Position message to traccar Position Entity.
     *
     * @param protoPosition
     * @return entityPosition
     */
    protected Position transfromPositionProtoToEntity(Traccar.Position protoPosition, long deviceId) {
        Position entityPosition = new Position(); // Entity
        entityPosition.setDeviceId(deviceId);
        entityPosition.setProtocol(getProtocolName());
        entityPosition.setLatitude(protoPosition.getLatitude());
        entityPosition.setLongitude(protoPosition.getLongitude());
        entityPosition.setAltitude(protoPosition.getAltitude());
        entityPosition.setAccuracy(protoPosition.getAccuracy());
        entityPosition.setCourse(protoPosition.getCourse());
        entityPosition.setDeviceTime(new Date(protoPosition.getDevicetime()));
        entityPosition.setFixTime(new Date(protoPosition.getFixtime()));
        entityPosition.setSpeed(protoPosition.getSpeed());
        entityPosition.setValid(true);  // !?

        entityPosition.set(Position.KEY_BATTERY, "low");

//      allow more than one events for one position ?
//      or create new position for each event ?
/*
        if exist !
        Traccar.Event event = protoPosition.getEvent(0);
        Traccar.EventType eventType = event.getEvent();
        if (eventType == EventType.KEY_ALARM) {
            if (event.getAlarm() == AlarmType.ALARM_SOS) {
                entityPosition.set(Position.KEY_ALARM, Position.ALARM_SOS);
            }
        }
 */
//      getLastLocation(entityPosition, null);  // sets validity ?
        return entityPosition;
    }

}
