package org.jeets.model.traccar.jpa;
// Generated 15.09.2017 14:36:40 by Hibernate Tools 4.3.5.Final

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * DeviceDriver generated by hbm2java
 */
@Entity
@Table(name = "device_driver", schema = "public")
public class DeviceDriver implements java.io.Serializable {

    private DeviceDriverId id;
    private Devices devices;
    private Drivers drivers;

    public DeviceDriver() {
    }

    public DeviceDriver(DeviceDriverId id, Devices devices, Drivers drivers) {
        this.id = id;
        this.devices = devices;
        this.drivers = drivers;
    }

    @EmbeddedId

    @AttributeOverrides({ @AttributeOverride(name = "deviceid", column = @Column(name = "deviceid", nullable = false)),
            @AttributeOverride(name = "driverid", column = @Column(name = "driverid", nullable = false)) })
    public DeviceDriverId getId() {
        return this.id;
    }

    public void setId(DeviceDriverId id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deviceid", nullable = false, insertable = false, updatable = false)
    public Devices getDevices() {
        return this.devices;
    }

    public void setDevices(Devices devices) {
        this.devices = devices;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driverid", nullable = false, insertable = false, updatable = false)
    public Drivers getDrivers() {
        return this.drivers;
    }

    public void setDrivers(Drivers drivers) {
        this.drivers = drivers;
    }

}