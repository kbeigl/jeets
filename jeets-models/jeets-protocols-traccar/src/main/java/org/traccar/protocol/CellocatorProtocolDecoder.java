/*
 * Copyright 2013 - 2019 Anton Tananaev (anton@traccar.org)
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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.NetworkMessage;
import org.traccar.Protocol;
import org.traccar.helper.DateBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Position;

import java.net.SocketAddress;

public class CellocatorProtocolDecoder extends BaseProtocolDecoder {

    public CellocatorProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    static final int MSG_CLIENT_STATUS = 0;
    static final int MSG_CLIENT_PROGRAMMING = 3;
    static final int MSG_CLIENT_SERIAL_LOG = 7;
    static final int MSG_CLIENT_SERIAL = 8;
    static final int MSG_CLIENT_MODULAR = 9;

    public static final int MSG_SERVER_ACKNOWLEDGE = 4;

    private byte commandCount;

    private void sendReply(Channel channel, SocketAddress remoteAddress, long deviceId, byte packetNumber) {
        if (channel != null) {
            ByteBuf reply = Unpooled.buffer(28);
            reply.writeByte('M');
            reply.writeByte('C');
            reply.writeByte('G');
            reply.writeByte('P');
            reply.writeByte(MSG_SERVER_ACKNOWLEDGE);
            reply.writeIntLE((int) deviceId);
            reply.writeByte(commandCount++);
            reply.writeIntLE(0); // authentication code
            reply.writeByte(0);
            reply.writeByte(packetNumber);
            reply.writeZero(11);

            byte checksum = 0;
            for (int i = 4; i < 27; i++) {
                checksum += reply.getByte(i);
            }
            reply.writeByte(checksum);

            channel.writeAndFlush(new NetworkMessage(reply, remoteAddress));
        }
    }

    private String decodeAlarm(short reason) {
        switch (reason) {
            case 70:
                return Position.ALARM_SOS;
            case 80:
                return Position.ALARM_POWER_CUT;
            case 81:
                return Position.ALARM_LOW_POWER;
            default:
                return null;
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;

        boolean alternative = buf.getByte(buf.readerIndex() + 3) != 'P';

        buf.skipBytes(4); // system code
        int type = buf.readUnsignedByte();
        long deviceUniqueId = buf.readUnsignedIntLE();

        if (type != MSG_CLIENT_SERIAL) {
            buf.readUnsignedShortLE(); // communication control
        }
        byte packetNumber = buf.readByte();

        sendReply(channel, remoteAddress, deviceUniqueId, packetNumber);

        if (type == MSG_CLIENT_STATUS) {

            Position position = new Position(getProtocolName());

            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, String.valueOf(deviceUniqueId));
            if (deviceSession == null) {
                return null;
            }
            position.setDeviceId(deviceSession.getDeviceId());

            position.set(Position.KEY_VERSION_HW, buf.readUnsignedByte());
            position.set(Position.KEY_VERSION_FW, buf.readUnsignedByte());
            buf.readUnsignedByte(); // protocol version

            position.set(Position.KEY_STATUS, buf.readUnsignedByte() & 0x0f);

            buf.readUnsignedByte(); // operator / configuration flags
            buf.readUnsignedByte(); // reason data
            position.set(Position.KEY_ALARM, decodeAlarm(buf.readUnsignedByte()));

            position.set("mode", buf.readUnsignedByte());
            position.set(Position.KEY_INPUT, buf.readUnsignedIntLE());

            if (alternative) {
                buf.readUnsignedByte(); // input
                position.set(Position.PREFIX_ADC + 1, buf.readUnsignedShortLE());
                position.set(Position.PREFIX_ADC + 2, buf.readUnsignedShortLE());
            } else {
                buf.readUnsignedByte(); // operator
                position.set(Position.PREFIX_ADC + 1, buf.readUnsignedIntLE());
            }

            position.set(Position.KEY_ODOMETER, buf.readUnsignedMediumLE());

            buf.skipBytes(6); // multi-purpose data
            buf.readUnsignedShortLE(); // fix time
            buf.readUnsignedByte(); // location status
            buf.readUnsignedByte(); // mode 1
            buf.readUnsignedByte(); // mode 2

            position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());

            position.setValid(true);

            if (alternative) {
                position.setLongitude(buf.readIntLE() / 10000000.0);
                position.setLatitude(buf.readIntLE() / 10000000.0);
            } else {
                position.setLongitude(buf.readIntLE() / Math.PI * 180 / 100000000);
                position.setLatitude(buf.readIntLE() / Math.PI * 180 / 100000000);
            }

            position.setAltitude(buf.readIntLE() * 0.01);

            if (alternative) {
                position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedIntLE()));
                position.setCourse(buf.readUnsignedShortLE() / 1000.0);
            } else {
                position.setSpeed(UnitsConverter.knotsFromMps(buf.readUnsignedIntLE() * 0.01));
                position.setCourse(buf.readUnsignedShortLE() / Math.PI * 180.0 / 1000.0);
            }

            DateBuilder dateBuilder = new DateBuilder()
                    .setTimeReverse(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte())
                    .setDateReverse(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedShortLE());
            position.setTime(dateBuilder.getDate());

            return position;
        }

        return null;
    }

}
