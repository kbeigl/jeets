package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="tc_groups")
@NamedQuery(name="Group.findAll", query="SELECT g FROM Group g")
public class Group implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tc_groups_id_gen")
	@SequenceGenerator(name="tc_groups_id_gen", sequenceName="tc_groups_id_seq")
	private Integer id;
	private String attributeString;
	private String name;

	public Group() {
	}

	@OneToMany(mappedBy="group")
	private List<Device> devices;

	@ManyToMany
	@JoinTable(name = "tc_group_attribute", 
		joinColumns = { @JoinColumn(name = "groupid") }, 
		inverseJoinColumns = { @JoinColumn(name = "attributeid") })
	private List<Attribute> attributes;

	@ManyToMany
	@JoinTable(name = "tc_group_command", 
		joinColumns = { @JoinColumn(name = "groupid") }, 
		inverseJoinColumns = { @JoinColumn(name = "commandid") })
	private List<Command> commands;

	@ManyToMany
	@JoinTable(name = "tc_group_driver", 
		joinColumns = { @JoinColumn(name = "groupid") }, 
		inverseJoinColumns = { @JoinColumn(name = "driverid") })
	private List<Driver> drivers;

	@ManyToMany
	@JoinTable(name = "tc_group_geofence", 
		joinColumns = { @JoinColumn(name = "groupid") }, 
		inverseJoinColumns = { @JoinColumn(name = "geofenceid") })
	private List<Geofence> geofences;

	@ManyToMany(mappedBy="groups")
	private List<Maintenance> maintenances;

	@ManyToMany(mappedBy="groups")
	private List<Notification> notifications;

	@ManyToOne
	@JoinColumn(name="groupid")
	private Group group;

	@OneToMany(mappedBy="group")
	private List<Group> groups;

	@ManyToMany(mappedBy="groups")
	private List<User> users;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAttributeString() {
		return attributeString;
	}

	public void setAttributeString(String attributeString) {
		this.attributeString = attributeString;
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

	public Device addDevice(Device device) {
		getDevices().add(device);
		device.setGroup(this);

		return device;
	}

	public Device removeDevice(Device device) {
		getDevices().remove(device);
		device.setGroup(null);

		return device;
	}

	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Command> getCommands() {
		return this.commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public List<Driver> getDrivers() {
		return this.drivers;
	}

	public void setDrivers(List<Driver> drivers) {
		this.drivers = drivers;
	}

	public List<Geofence> getGeofences() {
		return this.geofences;
	}

	public void setGeofences(List<Geofence> geofences) {
		this.geofences = geofences;
	}

	public List<Maintenance> getMaintenances() {
		return this.maintenances;
	}

	public void setMaintenances(List<Maintenance> maintenances) {
		this.maintenances = maintenances;
	}

	public List<Notification> getNotifications() {
		return this.notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public Group addGroup(Group group) {
		getGroups().add(group);
		group.setGroup(this);

		return group;
	}

	public Group removeGroup(Group group) {
		getGroups().remove(group);
		group.setGroup(null);

		return group;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}