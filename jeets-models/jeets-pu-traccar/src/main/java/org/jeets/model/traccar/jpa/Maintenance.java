package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "tc_maintenances")
public class Maintenance implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tc_maintenances_id_gen")
  @SequenceGenerator(name = "tc_maintenances_id_gen", sequenceName = "tc_maintenances_id_seq")
  private Integer id;

  private String attributes;
  private String name;
  private double period;
  private double start;
  private String type;

  public Maintenance() {}

  @ManyToMany
  @JoinTable(
      name = "tc_device_maintenance",
      joinColumns = {@JoinColumn(name = "maintenanceid")},
      inverseJoinColumns = {@JoinColumn(name = "deviceid")})
  private List<Device> devices;

  @ManyToMany
  @JoinTable(
      name = "tc_group_maintenance",
      joinColumns = {@JoinColumn(name = "maintenanceid")},
      inverseJoinColumns = {@JoinColumn(name = "groupid")})
  private List<Group> groups;

  @ManyToMany(mappedBy = "maintenances")
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

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPeriod() {
    return this.period;
  }

  public void setPeriod(double period) {
    this.period = period;
  }

  public double getStart() {
    return this.start;
  }

  public void setStart(double start) {
    this.start = start;
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

  public List<User> getUsers() {
    return this.users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  @Override
  public String toString() {
    final int maxLen = 3;
    return "Maintenance [id="
        + id
        + ", attributes="
        + attributes
        + ", name="
        + name
        + ", period="
        + period
        + ", start="
        + start
        + ", type="
        + type
        + ", devices="
        + (devices != null ? devices.subList(0, Math.min(devices.size(), maxLen)) : null)
        + ", groups="
        + (groups != null ? groups.subList(0, Math.min(groups.size(), maxLen)) : null)
        + ", users="
        + (users != null ? users.subList(0, Math.min(users.size(), maxLen)) : null)
        + "]";
  }
}
