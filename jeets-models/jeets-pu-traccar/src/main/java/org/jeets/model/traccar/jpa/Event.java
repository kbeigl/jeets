package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "tc_events")
public class Event implements Serializable {
  private static final long serialVersionUID = 1L;

  // TODO: create GeofenceManager/Test with positionid and geofenceid
  // with entity relations (see device) .. Georouter (introduced before Geofences)

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tc_events_id_gen")
  @SequenceGenerator(
      name = "tc_events_id_gen",
      sequenceName = "tc_events_id_seq",
      allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Integer id;

  private String type;
  private Date servertime;
  private Geofence geofence;
  private String attributes;
  private Integer maintenanceid;

  public Event() {}

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deviceid")
  private Device device;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "positionid")
  private Position position;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
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

  /* former relation with field
   * @Column(name = "positionid")
   * public Integer getPositionid() { return this.positionid; }
   * public void setPositionid(Integer positionid) { this.positionid = positionid; }
   */

  @Column(name = "geofenceid")
  public Geofence getGeofence() {
    return this.geofence;
  }

  public void setGeofence(Geofence geofence) {
    this.geofence = geofence;
  }

  /* @Column(name = "geofenceid")
   * public Integer getGeofenceid() { return this.geofenceid; }
   * public void setGeofenceid(Integer geofenceid) { this.geofenceid = geofenceid; }
   */

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

  @Column(name = "attributes", length = 4000)
  public String getAttributes() {
    return this.attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  public Integer getMaintenanceid() {
    return this.maintenanceid;
  }

  public void setMaintenanceid(Integer maintenanceid) {
    this.maintenanceid = maintenanceid;
  }

  @Override
  public String toString() {
    return "Event [id="
        + id
        + ", type="
        + type
        + ", servertime="
        + servertime
        + ", position="
        + position
        + ", geofence="
        + geofence
        + ", device="
        + device.getName()
        + "("
        + device.getUniqueid()
        + ")]";
  }
}
