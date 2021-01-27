package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "tc_calendars")
public class Calendar implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tc_calendars_id_gen")
  @SequenceGenerator(name = "tc_calendars_id_gen", sequenceName = "tc_calendars_id_seq")
  private Integer id;

  private String attributes;
  private byte[] data;
  private String name;

  public Calendar() {}

  @OneToMany(mappedBy = "calendar")
  private List<Geofence> geofences;

  @OneToMany(mappedBy = "calendar")
  private List<Notification> notifications;

  @ManyToMany(mappedBy = "calendars")
  private List<User> users;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getAttributes() {
    return this.attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  public byte[] getData() {
    return this.data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Geofence> getGeofences() {
    return this.geofences;
  }

  public void setGeofences(List<Geofence> geofences) {
    this.geofences = geofences;
  }

  public Geofence addGeofence(Geofence geofence) {
    getGeofences().add(geofence);
    geofence.setCalendar(this);
    return geofence;
  }

  public Geofence removeGeofence(Geofence geofence) {
    getGeofences().remove(geofence);
    geofence.setCalendar(null);
    return geofence;
  }

  public List<Notification> getNotifications() {
    return this.notifications;
  }

  public void setNotifications(List<Notification> notifications) {
    this.notifications = notifications;
  }

  public Notification addNotification(Notification notification) {
    getNotifications().add(notification);
    notification.setCalendar(this);
    return notification;
  }

  public Notification removeNotification(Notification notification) {
    getNotifications().remove(notification);
    notification.setCalendar(null);
    return notification;
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
    return "Calendar [id="
        + id
        + ", attributes="
        + attributes
        + ", data="
        + data
        + ", name="
        + name
        + ", geofences="
        + (geofences != null ? geofences.subList(0, Math.min(geofences.size(), maxLen)) : null)
        + ", notifications="
        + (notifications != null
            ? notifications.subList(0, Math.min(notifications.size(), maxLen))
            : null)
        + ", users="
        + (users != null ? users.subList(0, Math.min(users.size(), maxLen)) : null)
        + "]";
  }
}
