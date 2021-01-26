package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "tc_geofences")
@NamedQuery(name = "Geofence.findAll", query = "SELECT g FROM Geofence g")
public class Geofence implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tc_geofences_id_gen")
  @SequenceGenerator(name = "tc_geofences_id_gen", sequenceName = "tc_geofences_id_seq")
  private Integer id;

  private String area;
  private String attributes;
  private String description;
  private String name;

  public Geofence() {}

  @ManyToMany
  @JoinTable(
      name = "tc_device_geofence",
      joinColumns = {@JoinColumn(name = "geofenceid")},
      inverseJoinColumns = {@JoinColumn(name = "deviceid")})
  private List<Device> devices;

  @ManyToOne
  @JoinColumn(name = "calendarid")
  private Calendar calendar;

  @ManyToMany(mappedBy = "geofences")
  private List<Group> groups;

  @ManyToMany(mappedBy = "geofences")
  private List<User> users;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getArea() {
    return this.area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getAttributes() {
    return this.attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Device> getDevices() {
    return this.devices;
  }

  public void setDevices(List<Device> devices) {
    this.devices = devices;
  }

  public Calendar getCalendar() {
    return this.calendar;
  }

  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
  }

  public List<Group> getGroups() {
    return this.groups;
  }

  public void setGroups(List<Group> groups) {
    this.groups = groups;
  }

  public List<User> getUsers() {
    return this.users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  @Override
  public String toString() {
    final int maxLen = 3;
    return "Geofence [id="
        + id
        + ", area="
        + area
        + ", attributes="
        + attributes
        + ", description="
        + description
        + ", name="
        + name
        + ", devices="
        + (devices != null ? devices.subList(0, Math.min(devices.size(), maxLen)) : null)
        + ", calendar="
        + calendar
        + ", groups="
        + (groups != null ? groups.subList(0, Math.min(groups.size(), maxLen)) : null)
        + ", users="
        + (users != null ? users.subList(0, Math.min(users.size(), maxLen)) : null)
        + "]";
  }
}
