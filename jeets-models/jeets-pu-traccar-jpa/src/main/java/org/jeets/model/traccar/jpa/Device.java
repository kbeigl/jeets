package org.jeets.model.traccar.jpa;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "devices", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "uniqueid"))
@NamedQuery(name = "findDeviceByUniqueId", query = "SELECT d FROM Device d WHERE d.uniqueid = :uniqueid")
public class Device implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String uniqueid;
    private Date lastupdate;
    private Integer positionid;
    private String attributes;
    private String phone;
    private String model;
    private String contact;
    private String category;
    private Set<Event> events = new HashSet<Event>(0);
    private Set<Position> positions = new HashSet<Position>(0);
    private Set<DeviceGeofence> deviceGeofences = new HashSet<DeviceGeofence>(0);
//    not used for JeeTS yet
//    private Group group;
//    private Set<DeviceDriver> deviceDrivers = new HashSet<DeviceDriver>(0);
//    private Set<DeviceAttribute> deviceAttributes = new HashSet<DeviceAttribute>(0);
//    private Set<UserDevice> userDevices = new HashSet<UserDevice>(0);
    
    public Device() {
    }

//  TODO: remove constructors with explicit id !!
    public Device(int id, String name, String uniqueid) {
        this.id = id;
        this.name = name;
        this.uniqueid = uniqueid;
    }

    public Device(int id,   // Group groups, 
            String name, String uniqueid, Date lastupdate, Integer positionid,
            String attributes, String phone, String model, String contact, String category, 
//            Set<DeviceDriver> deviceDrivers, Set<DeviceAttribute> deviceAttributes, 
//            Set<UserDevice> userDevices, 
            Set<DeviceGeofence> deviceGeofences, 
//            Set<AttributeAlias> attributeAliaseses, 
            Set<Event> events, Set<Position> positions) {
        this.id = id;
//        this.group = groups;
        this.name = name;
        this.uniqueid = uniqueid;
        this.lastupdate = lastupdate;
        this.positionid = positionid;
        this.attributes = attributes;
        this.phone = phone;
        this.model = model;
        this.contact = contact;
        this.category = category;
//        this.deviceDrivers = deviceDrivers;
//        this.deviceAttributes = deviceAttributes;
//        this.userDevices = userDevices;
        this.deviceGeofences = deviceGeofences;
        this.events = events;
        this.positions = positions;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="devices_id_seq")
    @SequenceGenerator(name="devices_id_seq", sequenceName="devices_id_seq", allocationSize=1)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupid")
    public Group getGroups() {
        return this.group;
    }

    public void setGroups(Group groups) {
        this.group = groups;
    }
 */
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastupdate", length = 29)
    public Date getLastupdate() {
        return this.lastupdate;
    }

    public void setLastupdate(Date lastupdate) {
        this.lastupdate = lastupdate;
    }

    @Column(name = "positionid")
    public Integer getPositionid() {
        return this.positionid;
    }

    public void setPositionid(Integer positionid) {
        this.positionid = positionid;
    }

    @Column(name = "attributes", length = 4000)
    public String getAttributes() {
        return this.attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    @Column(name = "phone", length = 128)
    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "model", length = 128)
    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Column(name = "contact", length = 512)
    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Column(name = "category", length = 128)
    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

/*
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "devices")
    public Set<DeviceDriver> getDeviceDrivers() {
        return this.deviceDrivers;
    }

    public void setDeviceDrivers(Set<DeviceDriver> deviceDrivers) {
        this.deviceDrivers = deviceDrivers;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "devices")
    public Set<DeviceAttribute> getDeviceAttributes() {
        return this.deviceAttributes;
    }

    public void setDeviceAttributes(Set<DeviceAttribute> deviceAttributes) {
        this.deviceAttributes = deviceAttributes;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "devices")
    public Set<UserDevice> getUserDevices() {
        return this.userDevices;
    }

    public void setUserDevices(Set<UserDevice> userDevices) {
        this.userDevices = userDevices;
    }
 */
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "device")
    public Set<DeviceGeofence> getDeviceGeofences() {
        return this.deviceGeofences;
    }

    public void setDeviceGeofences(Set<DeviceGeofence> deviceGeofences) {
        this.deviceGeofences = deviceGeofences;
    }

//  device <> devices !?
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "devices", cascade = CascadeType.PERSIST)
    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "device", cascade = CascadeType.PERSIST)
    public Set<Position> getPositions() {
        return this.positions;
    }

    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    @Override
    public String toString() {
//      final int maxLen = 3;
        return "Device [id=" + id + ", name=" + name + ", uniqueid=" + uniqueid + ", lastupdate=" + lastupdate
                + ", positionid=" + positionid + ", attributes=" + attributes + ", phone=" + phone + ", model=" + model
                + ", contact=" + contact + ", category=" + category + ", ..."
//                + "events=" + (events != null ? toString(events, maxLen) : null) + ", positions="
//                + (positions != null ? toString(positions, maxLen) : null) + ", deviceGeofences="
//                + (deviceGeofences != null ? toString(deviceGeofences, maxLen) : null) 
                + "]";
    }

//    private String toString(Collection<?> collection, int maxLen) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("[");
//        int i = 0;
//        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
//            if (i > 0)
//                builder.append(", ");
//            builder.append(iterator.next());
//        }
//        builder.append("]");
//        return builder.toString();
//    }

}
