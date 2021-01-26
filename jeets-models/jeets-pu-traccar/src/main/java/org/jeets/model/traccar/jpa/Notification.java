package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "tc_notifications")
public class Notification implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tc_notifications_id_gen")
  @SequenceGenerator(name = "tc_notifications_id_gen", sequenceName = "tc_notifications_id_seq")
  private Integer id;

  private Boolean always;
  private String attributes;
  private String notificators;
  private String type;

  public Notification() {}

  @ManyToMany
  @JoinTable(
      name = "tc_device_notification",
      joinColumns = {@JoinColumn(name = "notificationid")},
      inverseJoinColumns = {@JoinColumn(name = "deviceid")})
  private List<Device> devices;

  @ManyToMany
  @JoinTable(
      name = "tc_group_notification",
      joinColumns = {@JoinColumn(name = "notificationid")},
      inverseJoinColumns = {@JoinColumn(name = "groupid")})
  private List<Group> groups;

  @ManyToOne
  @JoinColumn(name = "calendarid")
  private Calendar calendar;

  @ManyToMany(mappedBy = "notifications")
  private List<User> users;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Boolean getAlways() {
    return this.always;
  }

  public void setAlways(Boolean always) {
    this.always = always;
  }

  public String getAttributes() {
    return this.attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  public String getNotificators() {
    return this.notificators;
  }

  public void setNotificators(String notificators) {
    this.notificators = notificators;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Device> getDevices() {
    return this.devices;
  }

  public void setDevices(List<Device> devices) {
    this.devices = devices;
  }

  public List<Group> getGroups() {
    return this.groups;
  }

  public void setGroups(List<Group> groups) {
    this.groups = groups;
  }

  public Calendar getCalendar() {
    return this.calendar;
  }

  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
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
    return "Notification [id="
        + id
        + ", always="
        + always
        + ", attributes="
        + attributes
        + ", notificators="
        + notificators
        + ", type="
        + type
        + ", devices="
        + (devices != null ? devices.subList(0, Math.min(devices.size(), maxLen)) : null)
        + ", groups="
        + (groups != null ? groups.subList(0, Math.min(groups.size(), maxLen)) : null)
        + ", calendar="
        + calendar
        + ", users="
        + (users != null ? users.subList(0, Math.min(users.size(), maxLen)) : null)
        + "]";
  }
}
