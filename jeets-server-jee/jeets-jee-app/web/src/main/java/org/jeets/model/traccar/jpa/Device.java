package org.jeets.model.traccar.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<Position> positions = new ArrayList<Position>();
    
    public Device() {
    }

    public Device(int id, String name, String uniqueid) {
        this.id = id;
        this.name = name;
        this.uniqueid = uniqueid;
    }

    public Device(int id, 
            String name, String uniqueid, Date lastupdate, Integer positionid,
            String attributes, String phone, String model, String contact, String category, 
            Set<Event> events, List<Position> positions) {
        this.id = id;
        this.name = name;
        this.uniqueid = uniqueid;
        this.lastupdate = lastupdate;
        this.positionid = positionid;
        this.attributes = attributes;
        this.phone = phone;
        this.model = model;
        this.contact = contact;
        this.category = category;
        this.events = events;
        this.positions = positions;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueid() {
        return this.uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public Date getLastupdate() {
        return this.lastupdate;
    }

    public void setLastupdate(Date lastupdate) {
        this.lastupdate = lastupdate;
    }

    public Integer getPositionid() {
        return this.positionid;
    }

    public void setPositionid(Integer positionid) {
        this.positionid = positionid;
    }

    public String getAttributes() {
        return this.attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public List<Position> getPositions() {
        return this.positions;
    }

    public void setPositions(List<Position> positions) {
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

}
