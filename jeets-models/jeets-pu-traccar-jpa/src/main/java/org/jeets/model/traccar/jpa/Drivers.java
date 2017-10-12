package org.jeets.model.traccar.jpa;
// Generated 15.09.2017 14:36:40 by Hibernate Tools 4.3.5.Final

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Drivers generated by hbm2java
 */
@Entity
@Table(name = "drivers", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "uniqueid"))
public class Drivers implements java.io.Serializable {

    private int id;
    private String name;
    private String uniqueid;
    private String attributes;
    private Set<UserDriver> userDrivers = new HashSet<UserDriver>(0);
    private Set<DeviceDriver> deviceDrivers = new HashSet<DeviceDriver>(0);
    private Set<GroupDriver> groupDrivers = new HashSet<GroupDriver>(0);

    public Drivers() {
    }

    public Drivers(int id, String name, String uniqueid, String attributes) {
        this.id = id;
        this.name = name;
        this.uniqueid = uniqueid;
        this.attributes = attributes;
    }

    public Drivers(int id, String name, String uniqueid, String attributes, Set<UserDriver> userDrivers,
            Set<DeviceDriver> deviceDrivers, Set<GroupDriver> groupDrivers) {
        this.id = id;
        this.name = name;
        this.uniqueid = uniqueid;
        this.attributes = attributes;
        this.userDrivers = userDrivers;
        this.deviceDrivers = deviceDrivers;
        this.groupDrivers = groupDrivers;
    }

    @Id

    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false, length = 128)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "uniqueid", unique = true, nullable = false, length = 128)
    public String getUniqueid() {
        return this.uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    @Column(name = "attributes", nullable = false, length = 4000)
    public String getAttributes() {
        return this.attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "drivers")
    public Set<UserDriver> getUserDrivers() {
        return this.userDrivers;
    }

    public void setUserDrivers(Set<UserDriver> userDrivers) {
        this.userDrivers = userDrivers;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "drivers")
    public Set<DeviceDriver> getDeviceDrivers() {
        return this.deviceDrivers;
    }

    public void setDeviceDrivers(Set<DeviceDriver> deviceDrivers) {
        this.deviceDrivers = deviceDrivers;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "drivers")
    public Set<GroupDriver> getGroupDrivers() {
        return this.groupDrivers;
    }

    public void setGroupDrivers(Set<GroupDriver> groupDrivers) {
        this.groupDrivers = groupDrivers;
    }

}