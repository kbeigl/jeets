/*
 * Copyright 2012 - 2018 Anton Tananaev (anton@traccar.org)
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
package org.traccar;

import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.config.Config;
import org.traccar.database.ConnectionManager;
import org.traccar.database.IdentityManager;
//import org.traccar.database.StatisticsManager;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Device;
import org.traccar.model.Position;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public abstract class BaseProtocolDecoder extends ExtendedObjectDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProtocolDecoder.class);

    private static final String PROTOCOL_UNKNOWN = "unknown";

    private final Config config = Context.getConfig();
    private final IdentityManager identityManager = Context.getIdentityManager();
    private final ConnectionManager connectionManager = Context.getConnectionManager();
//    private final StatisticsManager statisticsManager = Context.getStatisticsManager();
    private final Protocol protocol;

    public BaseProtocolDecoder(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getProtocolName() {
        return protocol != null ? protocol.getName() : PROTOCOL_UNKNOWN;
    }

    public String getServer(Channel channel, char delimiter) {
        String server = config.getString(getProtocolName() + ".server");
        if (server == null && channel != null) {
            InetSocketAddress address = (InetSocketAddress) channel.localAddress();
            server = address.getAddress().getHostAddress() + ":" + address.getPort();
        }
        return server != null ? server.replace(':', delimiter) : null;
    }

    protected double convertSpeed(double value, String defaultUnits) {
        switch (config.getString(getProtocolName() + ".speed", defaultUnits)) {
            case "kmh":
                return UnitsConverter.knotsFromKph(value);
            case "mps":
                return UnitsConverter.knotsFromMps(value);
            case "mph":
                return UnitsConverter.knotsFromMph(value);
            case "kn":
            default:
                return value;
        }
    }

    protected TimeZone getTimeZone(long deviceId) {
        return getTimeZone(deviceId, "UTC");
    }

    protected TimeZone getTimeZone(long deviceId, String defaultTimeZone) {
        TimeZone result = TimeZone.getTimeZone(defaultTimeZone);
        String timeZoneName = identityManager.lookupAttributeString(deviceId, "decoder.timezone", null, false, true);
        if (timeZoneName != null) {
            result = TimeZone.getTimeZone(timeZoneName);
        } else {
            int timeZoneOffset = config.getInteger(getProtocolName() + ".timezone", 0);
            if (timeZoneOffset != 0) {
                result.setRawOffset(timeZoneOffset * 1000);
                LOGGER.warn("Config parameter " + getProtocolName() + ".timezone is deprecated");
            }
        }
        return result;
    }

    private DeviceSession channelDeviceSession; // connection-based protocols
    private Map<SocketAddress, DeviceSession> addressDeviceSessions = new HashMap<>(); // connectionless protocols

    private long findDeviceId(SocketAddress remoteAddress, String... uniqueIds) {
        if (uniqueIds.length > 0) {
            long deviceId = 0;
            Device device = null;
            try {
                for (String uniqueId : uniqueIds) {
                    if (uniqueId != null) {
                        device = identityManager.getByUniqueId(uniqueId);
                        if (device != null) {
                            deviceId = device.getId();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Find device error", e);
            }
            if (deviceId == 0 && config.getBoolean("database.registerUnknown")) {
                return identityManager.addUnknownDevice(uniqueIds[0]);
            }
            if (device != null && !device.getDisabled() || config.getBoolean("database.storeDisabled")) {
                return deviceId;
            }
            StringBuilder message = new StringBuilder();
            if (deviceId == 0) {
                message.append("Unknown device -");
            } else {
                message.append("Disabled device -");
            }
            for (String uniqueId : uniqueIds) {
                message.append(" ").append(uniqueId);
            }
            if (remoteAddress != null) {
                message.append(" (").append(((InetSocketAddress) remoteAddress).getHostString()).append(")");
            }
            LOGGER.warn(message.toString());
        }
        return 0;
    }

    public DeviceSession getDeviceSession(Channel channel, SocketAddress remoteAddress, String... uniqueIds) {
        return getDeviceSession(channel, remoteAddress, false, uniqueIds);
    }

    public DeviceSession getDeviceSession(
            Channel channel, SocketAddress remoteAddress, boolean ignoreCache, String... uniqueIds) {
        if (channel != null && BasePipelineFactory.getHandler(channel.pipeline(), HttpRequestDecoder.class) != null
                || ignoreCache || config.getBoolean(getProtocolName() + ".ignoreSessionCache")
                || config.getBoolean("decoder.ignoreSessionCache")) {
            long deviceId = findDeviceId(remoteAddress, uniqueIds);
            if (deviceId != 0) {
                if (connectionManager != null) {
                    connectionManager.addActiveDevice(deviceId, protocol, channel, remoteAddress);
                }
                return new DeviceSession(deviceId);
            } else {
                return null;
            }
        }
        if (channel instanceof DatagramChannel) {
            long deviceId = findDeviceId(remoteAddress, uniqueIds);
            DeviceSession deviceSession = addressDeviceSessions.get(remoteAddress);
            if (deviceSession != null && (deviceSession.getDeviceId() == deviceId || uniqueIds.length == 0)) {
                return deviceSession;
            } else if (deviceId != 0) {
                deviceSession = new DeviceSession(deviceId);
                addressDeviceSessions.put(remoteAddress, deviceSession);
                if (connectionManager != null) {
                    connectionManager.addActiveDevice(deviceId, protocol, channel, remoteAddress);
                }
                return deviceSession;
            } else {
                return null;
            }
        } else {
            if (channelDeviceSession == null) {
                long deviceId = findDeviceId(remoteAddress, uniqueIds);
                if (deviceId != 0) {
                    channelDeviceSession = new DeviceSession(deviceId);
                    if (connectionManager != null) {
                        connectionManager.addActiveDevice(deviceId, protocol, channel, remoteAddress);
                    }
                }
            }
            return channelDeviceSession;
        }
    }

    public void getLastLocation(Position position, Date deviceTime) {
        if (position.getDeviceId() != 0) {
            position.setOutdated(true);

            Position last = identityManager.getLastPosition(position.getDeviceId());
            if (last != null) {
                position.setFixTime(last.getFixTime());
                position.setValid(last.getValid());
                position.setLatitude(last.getLatitude());
                position.setLongitude(last.getLongitude());
                position.setAltitude(last.getAltitude());
                position.setSpeed(last.getSpeed());
                position.setCourse(last.getCourse());
                position.setAccuracy(last.getAccuracy());
            } else {
                position.setFixTime(new Date(0));
            }

            if (deviceTime != null) {
                position.setDeviceTime(deviceTime);
            } else {
                position.setDeviceTime(new Date());
            }
        }
    }

    @Override
    protected void onMessageEvent(
            Channel channel, SocketAddress remoteAddress, Object originalMessage, Object decodedMessage) {
//        if (statisticsManager != null) {
//            statisticsManager.registerMessageReceived();
//        }
        Position position = null;
        if (decodedMessage != null) {
            if (decodedMessage instanceof Position) {
                position = (Position) decodedMessage;
            } else if (decodedMessage instanceof Collection) {
                Collection positions = (Collection) decodedMessage;
                if (!positions.isEmpty()) {
                    position = (Position) positions.iterator().next();
                }
            }
        }
        if (position != null) {
            connectionManager.updateDevice(
                    position.getDeviceId(), Device.STATUS_ONLINE, new Date());
        } else {
            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
            if (deviceSession != null) {
                connectionManager.updateDevice(
                        deviceSession.getDeviceId(), Device.STATUS_ONLINE, new Date());
            }
        }
    }

    @Override
    protected Object handleEmptyMessage(Channel channel, SocketAddress remoteAddress, Object msg) {
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
        if (config.getBoolean("database.saveEmpty") && deviceSession != null) {
            Position position = new Position(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());
            getLastLocation(position, null);
            return position;
        } else {
            return null;
        }
    }

}
