package org.jeets.model.traccar.jpa;
// Generated 20.02.2017 21:12:15 by Hibernate Tools 4.3.5.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Events generated by hbm2java
 */
@Entity
@Table(name = "events", schema = "public")
public class Event implements java.io.Serializable {

    private int id;
    private Device device;
    private String type;
    private Date servertime;
    private Integer positionid;
    private Integer geofenceid;
    private String attributes;

    public Event() {
    }

    public Event(int id, String type, Date servertime) {
        this.id = id;
        this.type = type;
        this.servertime = servertime;
    }

    public Event(int id, Device devices, String type, Date servertime, Integer positionid, Integer geofenceid,
            String attributes) {
        this.id = id;
        this.device = devices;
        this.type = type;
        this.servertime = servertime;
        this.positionid = positionid;
        this.geofenceid = geofenceid;
        this.attributes = attributes;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="events_id_seq")
    @SequenceGenerator(name="events_id_seq", sequenceName="events_id_seq", allocationSize=1)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deviceid")
    public Device getDevices() {
        return this.device;
    }

    public void setDevices(Device devices) {
        this.device = devices;
    }

    @Column(name = "type", nullable = false, length = 128)
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "servertime", nullable = false, length = 29)
    public Date getServertime() {
        return this.servertime;
    }

    public void setServertime(Date servertime) {
        this.servertime = servertime;
    }

    @Column(name = "positionid")
    public Integer getPositionid() {
        return this.positionid;
    }

    public void setPositionid(Integer positionid) {
        this.positionid = positionid;
    }

    @Column(name = "geofenceid")
    public Integer getGeofenceid() {
        return this.geofenceid;
    }

    public void setGeofenceid(Integer geofenceid) {
        this.geofenceid = geofenceid;
    }

    @Column(name = "attributes", length = 4000)
    public String getAttributes() {
        return this.attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

}