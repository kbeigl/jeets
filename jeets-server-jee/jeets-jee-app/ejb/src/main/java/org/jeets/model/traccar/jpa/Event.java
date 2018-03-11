package org.jeets.model.traccar.jpa;
// Generated 20.02.2017 21:12:15 by Hibernate Tools 4.3.5.Final

import java.util.Date;

public class Event implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private Device device;
    private String type;
    private Date servertime;
    private Position position;
    private String attributes;

    public Event() {
    }

    public Event(int id, String type, Date servertime) {
        this.id = id;
        this.type = type;
        this.servertime = servertime;
    }

    public Event(int id, Device device, String type, Date servertime, 
            Position position, String attributes) {
        this.id = id;
        this.device = device;
        this.type = type;
        this.servertime = servertime;
        this.position = position;
        this.attributes = attributes;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getServertime() {
        return this.servertime;
    }

    public void setServertime(Date servertime) {
        this.servertime = servertime;
    }

    public String getAttributes() {
        return this.attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "Event [id=" + id + ", device=" + device + ", type=" + type 
                + ", servertime=" + servertime + ", position=" + position 
                + ", attributes=" + attributes 
                + "]";
    }

}
