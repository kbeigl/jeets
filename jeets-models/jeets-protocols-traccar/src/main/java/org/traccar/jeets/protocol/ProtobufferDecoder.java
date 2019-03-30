/*
 * Copyright 2013 - 2018 Anton Tananaev (anton@traccar.org)
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
package org.traccar.jeets.protocol;

import io.netty.channel.Channel;

import org.jeets.protocol.Traccar;
import org.traccar.DeviceSession;
import org.traccar.NetworkMessage;
import org.traccar.jeets.BaseProtocolDecoder;
import org.traccar.jeets.Protocol;

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
public class ProtobufferDecoder extends BaseProtocolDecoder {

    public ProtobufferDecoder(Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Traccar.Device protoDevice = (Traccar.Device) msg; // extract protobuffer
        System.out.println("received device: " + protoDevice.getUniqueid()
        + " with " + protoDevice.getPositionCount() + " positions");

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, protoDevice.getUniqueid());
        if (deviceSession == null) {
            return null;
        }
        long deviceId = deviceSession.getDeviceId();

        List<org.traccar.jeets.model.Position> positions =
                transformDeviceProtoToPositionEntities(protoDevice, deviceId);

        // send ACK after successful transformation
        if (deviceSession != null) {
            if (channel != null) {
                Traccar.Acknowledge.Builder ackBuilder = Traccar.Acknowledge.newBuilder();
                ackBuilder.setDeviceid(456);
                channel.writeAndFlush(new NetworkMessage(ackBuilder.build(), remoteAddress));
//              System.out.println("server responded with ack: " + ackBuilder.toString());
            }
        }

        // implement Event !!

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
     *
     * @param protoDevice
     * @param deviceId
     * @return entityPositions
     */
    private List<org.traccar.jeets.model.Position>
    transformDeviceProtoToPositionEntities(Traccar.Device protoDevice, long deviceId) {
        List<org.traccar.jeets.model.Position> entityPositions = new ArrayList<org.traccar.jeets.model.Position>();
        for (int pos = 0; pos < protoDevice.getPositionCount(); pos++) {
            org.traccar.jeets.model.Position entityPosition =
                    transfromPositionProtoToEntity(protoDevice.getPosition(pos), deviceId);
            entityPositions.add(entityPosition);
        }
        return entityPositions;
    }

    protected org.traccar.jeets.model.Position
    transfromPositionProtoToEntity(Traccar.Position protoPosition, long deviceId) {

        org.traccar.jeets.model.Position entityPosition =
                new org.traccar.jeets.model.Position(getProtocolName());
        entityPosition.setDeviceId(deviceId);
        entityPosition.setLatitude(protoPosition.getLatitude());
        entityPosition.setLongitude(protoPosition.getLongitude());
        entityPosition.setAltitude(protoPosition.getAltitude());
        entityPosition.setAccuracy(protoPosition.getAccuracy());
        entityPosition.setCourse(protoPosition.getCourse());

        // Date devTime = new Date(protoPosition.getDevicetime());
        // System.out.println("proto device time: " + devTime );
        Date fixTime = new Date(protoPosition.getFixtime());
//      System.out.println("proto device fixtime: " + fixTime);

        entityPosition.setDeviceTime(new Date(protoPosition.getDevicetime()));
        entityPosition.setFixTime(fixTime);
        entityPosition.setSpeed(protoPosition.getSpeed());
        entityPosition.setValid(true); // !?

        // protoPosition.getEventCount(); ...
        // protoPosition.getEvent(index); ...
        entityPosition.set(org.traccar.jeets.model.Position.KEY_BATTERY, "low");
        // stored in position.attributes (not showing in frontend):
        // "{"battery":"low","distance":415.41,"totalDistance":415.41,"motion":false}"

        // allow more than one events for one position ?
        // or create new position for each event ?
        /*
         * if exist ! Traccar.Event event = protoPosition.getEvent(0); Traccar.EventType
         * eventType = event.getEvent(); if (eventType == EventType.KEY_ALARM) { if
         * (event.getAlarm() == AlarmType.ALARM_SOS) {
         * entityPosition.set(Position.KEY_ALARM, Position.ALARM_SOS); } }
         */
        // sets fixtime to Date(0) (1970) for FIRST device message in db??
        // with a PREVIOUS position in db IT WORKS
//        getLastLocation(entityPosition, null);
        return entityPosition;
    }

}
