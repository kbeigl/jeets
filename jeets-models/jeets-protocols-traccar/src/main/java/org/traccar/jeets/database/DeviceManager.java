/*
 * Copyright 2016 - 2018 Anton Tananaev (anton@traccar.org)
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
package org.traccar.jeets.database;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.jeets.Config;
import org.traccar.jeets.Context;
import org.traccar.jeets.model.DeviceState;
import org.traccar.jeets.model.Position;
import org.traccar.model.BaseModel;
import org.traccar.model.Device;
import org.traccar.model.DeviceAccumulators;

public class DeviceManager implements IdentityManager {
//  extends BaseObjectManager
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManager.class);

    public static final long DEFAULT_REFRESH_DELAY = 300;

    private final Config config;
    private final long dataRefreshDelay;

    private Map<String, Device> devicesByUniqueId;
    private Map<String, Device> devicesByPhone;
    private AtomicLong devicesLastUpdate = new AtomicLong();

    private final Map<Long, Position> positions = new ConcurrentHashMap<>();

    private final Map<Long, DeviceState> deviceStates = new ConcurrentHashMap<>();

    public DeviceManager() {
        super();    // nil
        this.config = Context.getConfig();
        if (devicesByPhone == null) {
            devicesByPhone = new ConcurrentHashMap<>();
        }
        if (devicesByUniqueId == null) {
            devicesByUniqueId = new ConcurrentHashMap<>();
        }
        dataRefreshDelay = config.getLong("database.refreshDelay", DEFAULT_REFRESH_DELAY) * 1000;
        refreshLastPositions();
    }

    @Override
    public long addUnknownDevice(String uniqueId) {
        Device device = new Device();
        device.setName(uniqueId);
        device.setUniqueId(uniqueId);
        device.setCategory(Context.getConfig().getString("database.registerUnknown.defaultCategory"));

        addItem(device);
        LOGGER.info("Registered unknown device {} [id={}]", uniqueId, device.getId());
        return device.getId();
    }

    public void updateDeviceCache(boolean force) {
        long lastUpdate = devicesLastUpdate.get();
        if ((force || System.currentTimeMillis() - lastUpdate > dataRefreshDelay)
                && devicesLastUpdate.compareAndSet(lastUpdate, System.currentTimeMillis())) {
            refreshItems();
        }
    }

    @Override
    public Device getByUniqueId(String uniqueId) {
        boolean forceUpdate = !devicesByUniqueId.containsKey(uniqueId) && !config.getBoolean("database.ignoreUnknown");

        updateDeviceCache(forceUpdate);

        return devicesByUniqueId.get(uniqueId);
    }

    public Device getDeviceByPhone(String phone) {
        return devicesByPhone.get(phone);
    }

    public Set<Long> getAllItems() {
        Set<Long> result = devices.keySet();
        if (result.isEmpty()) {
            updateDeviceCache(true);
            result = devices.keySet();
        }
        return result;
    }

    private void putUniqueDeviceId(Device device) {
        if (devicesByUniqueId == null) {
            devicesByUniqueId = new ConcurrentHashMap<>(getAllItems().size());
        }
        devicesByUniqueId.put(device.getUniqueId(), device);
    }

    private void putPhone(Device device) {
        if (devicesByPhone == null) {
            devicesByPhone = new ConcurrentHashMap<>(getAllItems().size());
        }
        devicesByPhone.put(device.getPhone(), device);
    }

    protected void addNewItem(Device device) {
        LOGGER.info("add Device {}", device);
        devices.put(device.getId(), device);
        putUniqueDeviceId(device);
        if (device.getPhone() != null  && !device.getPhone().isEmpty()) {
            putPhone(device);
        }
    }

    protected void updateCachedItem(Device device) {
        Device cachedDevice = getById(device.getId());
        cachedDevice.setName(device.getName());
        cachedDevice.setGroupId(device.getGroupId());
        cachedDevice.setCategory(device.getCategory());
        cachedDevice.setContact(device.getContact());
        cachedDevice.setModel(device.getModel());
        cachedDevice.setDisabled(device.getDisabled());
        cachedDevice.setAttributes(device.getAttributes());
        if (!device.getUniqueId().equals(cachedDevice.getUniqueId())) {
            devicesByUniqueId.remove(cachedDevice.getUniqueId());
            cachedDevice.setUniqueId(device.getUniqueId());
            putUniqueDeviceId(cachedDevice);
        }
        if (device.getPhone() != null && !device.getPhone().isEmpty()
                && !device.getPhone().equals(cachedDevice.getPhone())) {
            String phone = cachedDevice.getPhone();
            if (phone != null && !phone.isEmpty()) {
                devicesByPhone.remove(phone);
            }
            cachedDevice.setPhone(device.getPhone());
            putPhone(cachedDevice);
        }
    }

//  saved from BaseObjectMgr, but overridden
//    protected void updateCachedItem(Device item) {
//        devices.put(item.getId(), item);
//    }

    protected void removeCachedItem(long deviceId) {
        Device cachedDevice = getById(deviceId);
        if (cachedDevice != null) {
            String deviceUniqueId = cachedDevice.getUniqueId();
            String phone = cachedDevice.getPhone();
            devices.remove(deviceId);
            devicesByUniqueId.remove(deviceUniqueId);
            if (phone != null && !phone.isEmpty()) {
                devicesByPhone.remove(phone);
            }
        }
        positions.remove(deviceId);
    }

    public void updateDeviceStatus(Device device) {
        LOGGER.warn("updateDeviceStatus error");
    }

    private void refreshLastPositions() {
        LOGGER.warn("refreshLastPositions error");
    }

    public boolean isLatestPosition(Position position) {
        Position lastPosition = getLastPosition(position.getDeviceId());
        return lastPosition == null || position.getFixTime().compareTo(lastPosition.getFixTime()) >= 0;
    }

    public void updateLatestPosition(Position position) {
        LOGGER.warn("updateLatestPosition error");
    }

    @Override
    public Position getLastPosition(long deviceId) {
        return positions.get(deviceId);
    }

    public Collection<Position> getInitialState(long userId) {
        List<Position> result = new LinkedList<>();
        return result;
    }

    public boolean lookupAttributeBoolean(
            long deviceId, String attributeName, boolean defaultValue, boolean lookupConfig) {
        Object result = lookupAttribute(deviceId, attributeName, lookupConfig);
        if (result != null) {
            return result instanceof String ? Boolean.parseBoolean((String) result) : (Boolean) result;
        }
        return defaultValue;
    }

    public String lookupAttributeString(
            long deviceId, String attributeName, String defaultValue, boolean lookupConfig) {
        Object result = lookupAttribute(deviceId, attributeName, lookupConfig);
        return result != null ? (String) result : defaultValue;
    }

    public int lookupAttributeInteger(long deviceId, String attributeName, int defaultValue, boolean lookupConfig) {
        Object result = lookupAttribute(deviceId, attributeName, lookupConfig);
        if (result != null) {
            return result instanceof String ? Integer.parseInt((String) result) : ((Number) result).intValue();
        }
        return defaultValue;
    }

    public long lookupAttributeLong(
            long deviceId, String attributeName, long defaultValue, boolean lookupConfig) {
        Object result = lookupAttribute(deviceId, attributeName, lookupConfig);
        if (result != null) {
            return result instanceof String ? Long.parseLong((String) result) : ((Number) result).longValue();
        }
        return defaultValue;
    }

    public double lookupAttributeDouble(
            long deviceId, String attributeName, double defaultValue, boolean lookupConfig) {
        Object result = lookupAttribute(deviceId, attributeName, lookupConfig);
        if (result != null) {
            return result instanceof String ? Double.parseDouble((String) result) : ((Number) result).doubleValue();
        }
        return defaultValue;
    }

    private Object lookupAttribute(long deviceId, String attributeName, boolean lookupConfig) {
        Object result = null;
        Device device = getById(deviceId);
        if (device != null) {
            result = device.getAttributes().get(attributeName);
        }
        return result;
    }

    public void resetDeviceAccumulators(DeviceAccumulators deviceAccumulators) {
        LOGGER.warn("resetDeviceAccumulators error");
    }

    public DeviceState getDeviceState(long deviceId) {
        DeviceState deviceState = deviceStates.get(deviceId);
        if (deviceState == null) {
            deviceState = new DeviceState();
            deviceStates.put(deviceId, deviceState);
        }
        return deviceState;
    }

    public void setDeviceState(long deviceId, DeviceState deviceState) {
        deviceStates.put(deviceId, deviceState);
    }

    private Map<Long, Device> devices;

//  BaseObjectManager() { ---------------------------------

    public Device getById(long itemId) {
        return devices.get(itemId);
    }

    public void refreshItems() {
        if (devices == null) {
            devices = new ConcurrentHashMap<>();
        }
    }

    /* simulate ID generation of EntityManager for runtime only */
    private static long itemId = 0;

    public void addItem(Device item) {
        item.setId(++itemId);
        addNewItem(item);
    }

    public void updateItem(Device item) {
        updateCachedItem(item);
    }

    public void removeItem(long itemId) {
        LOGGER.info("remove Device.id {}", itemId);
        BaseModel item = (BaseModel) getById(itemId);
        if (item != null) {
            removeCachedItem(itemId);
        }
    }

}
