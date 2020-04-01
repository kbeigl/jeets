/*
 * Copyright 2015 - 2018 Anton Tananaev (anton@traccar.org)
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
package org.traccar.database;

import io.netty.channel.Channel;
import io.netty.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.Context;
import org.traccar.Protocol;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    private final boolean enableStatusEvents;
    private final boolean updateDeviceState;

    private final Map<Long, ActiveDevice> activeDevices = new ConcurrentHashMap<>();
    private final Map<Long, Timeout> timeouts = new ConcurrentHashMap<>();

    public ConnectionManager() {
        enableStatusEvents = Context.getConfig().getBoolean("event.enable");
        updateDeviceState = Context.getConfig().getBoolean("status.updateDeviceState");
    }

    public void addActiveDevice(long deviceId, Protocol protocol, Channel channel, SocketAddress remoteAddress) {
        activeDevices.put(deviceId, new ActiveDevice(deviceId, protocol, channel, remoteAddress));
    }

    public void removeActiveDevice(Channel channel) {
        for (ActiveDevice activeDevice : activeDevices.values()) {
            if (activeDevice.getChannel() == channel) {
                updateDevice(activeDevice.getDeviceId(), Device.STATUS_OFFLINE, null);
                activeDevices.remove(activeDevice.getDeviceId());
                break;
            }
        }
    }

    public ActiveDevice getActiveDevice(long deviceId) {
        return activeDevices.get(deviceId);
    }

    public void updateDevice(final long deviceId, String status, Date time) {
        Device device = Context.getIdentityManager().getById(deviceId);
        if (device == null) {
            return;
        }

        String oldStatus = device.getStatus();
        device.setStatus(status);

        if (enableStatusEvents && !status.equals(oldStatus)) {
            String eventType;
            Map<Event, Position> events = new HashMap<>();
            switch (status) {
                case Device.STATUS_ONLINE:
                    eventType = Event.TYPE_DEVICE_ONLINE;
                    break;
                case Device.STATUS_UNKNOWN:
                    eventType = Event.TYPE_DEVICE_UNKNOWN;
                    if (updateDeviceState) {
                        events.putAll(updateDeviceState(deviceId));
                    }
                    break;
                default:
                    eventType = Event.TYPE_DEVICE_OFFLINE;
                    if (updateDeviceState) {
                        events.putAll(updateDeviceState(deviceId));
                    }
                    break;
            }
            events.put(new Event(eventType, deviceId), null);
        }

        Timeout timeout = timeouts.remove(deviceId);
        if (timeout != null) {
            timeout.cancel();
        }

        if (time != null) {
            device.setLastUpdate(time);
        }

        if (status.equals(Device.STATUS_ONLINE)) {
            LOGGER.info("updateDevice '" + device.getUniqueId() + "' to status ONLINE");
        }

        Context.getDeviceManager().updateDeviceStatus(device);
        updateDevice(device);
    }

    public Map<Event, Position> updateDeviceState(long deviceId) {
        Map<Event, Position> result = new HashMap<>();
        return result;
    }

    public synchronized void updateDevice(Device device) {
        LOGGER.debug("ConnectionManager.updateDevice not implemented");
    }

    public synchronized void updatePosition(Position position) {
        LOGGER.debug("ConnectionManager.updatePosition not implemented");
    }

}
