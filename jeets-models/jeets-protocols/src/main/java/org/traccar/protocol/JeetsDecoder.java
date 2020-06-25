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
package org.traccar.protocol;

import io.netty.channel.Channel;

import org.jeets.protobuf.Jeets;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.NetworkMessage;
import org.traccar.Protocol;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Protobuffer Decoder from Device messages
 * with Positions and Events to Traccar Entities.
 * Tested with Traccar 4.2 (and Netty 4)
 *
 * @author Kristof Beiglböck kbeigl@jeets.org
 */
public class JeetsDecoder extends BaseProtocolDecoder {

    public JeetsDecoder(Protocol protocol) {
        super(protocol);
    }

	@Override
	protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

		Jeets.Device protoDevice = (Jeets.Device) msg; // extract protobuffer
		System.out.println("received device:\n" + protoDevice.toString());

		DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, protoDevice.getUniqueid());
		if (deviceSession == null) {
			return null;
		}
		long deviceId = deviceSession.getDeviceId();

		List<org.traccar.model.Position> positions = transformDeviceProtoToPositionEntities(protoDevice, deviceId);

		// send ACK after successful transformation
		if (deviceSession != null) {
			if (channel != null) {
				Jeets.Acknowledge.Builder ackBuilder = Jeets.Acknowledge.newBuilder();
				ackBuilder.setDeviceid(123);
				channel.writeAndFlush(new NetworkMessage(ackBuilder.build(), remoteAddress));
				System.out.println("responded with ack:\n" + ackBuilder.toString());
			}
		}
		
//		implement Event !!

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
    private List<org.traccar.model.Position> 
    transformDeviceProtoToPositionEntities(Jeets.Device protoDevice, long deviceId) {
        List<org.traccar.model.Position> entityPositions = new ArrayList<org.traccar.model.Position>();
        for (int pos = 0; pos < protoDevice.getPositionCount(); pos++) {
        	org.traccar.model.Position entityPosition = 
        			transfromPositionProtoToEntity(protoDevice.getPosition(pos), deviceId);
            entityPositions.add(entityPosition);
        }
        return entityPositions;
    }

    protected org.traccar.model.Position 
    transfromPositionProtoToEntity(Jeets.Position protoPosition, long deviceId) {

    	org.traccar.model.Position entityPosition = new org.traccar.model.Position(getProtocolName());
    	entityPosition.setDeviceId(deviceId);
        entityPosition.setLatitude(protoPosition.getLatitude());
        entityPosition.setLongitude(protoPosition.getLongitude());
        entityPosition.setAltitude(protoPosition.getAltitude());
        entityPosition.setAccuracy(protoPosition.getAccuracy());
        entityPosition.setCourse(protoPosition.getCourse());
        entityPosition.setDeviceTime(new Date(protoPosition.getDevicetime()));
        Date fixTime = new Date(protoPosition.getFixtime());
        entityPosition.setFixTime(fixTime);
        entityPosition.setSpeed(protoPosition.getSpeed());
        entityPosition.setValid(true);  // !?

//      protoPosition.getEventCount(); ...
//      protoPosition.getEvent(index); ...
        entityPosition.set(org.traccar.model.Position.KEY_BATTERY, "low");
//      stored in position.attributes (not showing in frontend): 
//      "{"battery":"low","distance":415.41,"totalDistance":415.41,"motion":false}"

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
//      sets fixtime to Date(0) (1970) for first device message in db??
//      with a previous position in db it works 
        getLastLocation(entityPosition, null);
        return entityPosition;
    }

}
