package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Entity(name = "Device")
@Table(name = "tc_devices", uniqueConstraints = @UniqueConstraint(columnNames = "uniqueid"))
@NamedQuery(name = "findDeviceByUniqueId", query = "SELECT d FROM Device d WHERE d.uniqueid = :uniqueid")
public class Device implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tc_devices_id_gen")
	@SequenceGenerator(name = "tc_devices_id_gen", sequenceName = "tc_devices_id_seq", allocationSize = 1)
	@Column(name = "id", updatable = false, unique = true, nullable = false)
	private Integer id;
	private String name;
	private String uniqueid;
	private Date lastupdate;
	private String attributeString;
	private String phone;
	private String model;
	private String contact;
	private String category;
	private Boolean disabled;
	
	public Device() {
	}

	/**
	 * This is a virtual database relation. There are no PK, FK relations in the ERM.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "positionid")
	private Position lastPosition;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groupid")
	private Group group;

//	stackoverflow.com/questions/37243159
//	model ManyToMany relations from counter Entity, owner ?
//	try to avoid extra join table for this (unrelated!) Device
//	test and monitor sql (for Device without relations)
	@ManyToMany(mappedBy = "devices", fetch = FetchType.LAZY)
	private List<Driver> drivers;

	@ManyToMany // fetch = FetchType.LAZY ?
	@JoinTable(name = "tc_device_attribute", 
		joinColumns = { @JoinColumn(name = "deviceid", nullable=false) }, 
		inverseJoinColumns = { @JoinColumn(name = "attributeid", nullable=false) })
	private Set<Attribute> attributes = new HashSet<>();

	@ManyToMany(mappedBy = "devices")
	private List<User> users;

	@ManyToMany(mappedBy = "devices", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Geofence> geofences = new HashSet<Geofence>(0);

	@OneToMany(mappedBy = "device", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Set<Event> events = new HashSet<Event>(0);

    /**
     * List of Positions is returned in DESCending order. <br>
     * LAST chronological Position is FIRST in the List! <br>
     * Use FIFO mechanism for large Lists to profit from LAZY loading!
     * <p>
     * Identical timestamps could be disambiguated with IDs to determine exact
     * submission order, but should not be implemented for a production system.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "device", cascade = CascadeType.PERSIST)
    @OrderBy ("fixtime DESC, devicetime DESC, servertime DESC")
    private List<Position> positions = new ArrayList<Position>();

	@ManyToMany
	@JoinTable(name = "tc_device_command", 
		joinColumns = { @JoinColumn(name = "deviceid") }, 
		inverseJoinColumns = { @JoinColumn(name = "commandid") })
	private List<Command> commands;

	@ManyToMany(mappedBy = "devices")
	private List<Maintenance> maintenances;

	@ManyToMany(mappedBy = "devices")
	private List<Notification> notifications;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Position getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(Position lastPosition) {
		this.lastPosition = lastPosition;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

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
	@Column(name = "lastupdate")
	public Date getLastupdate() {
		return this.lastupdate;
	}

	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}

	@Column(name = "attributes", length = 4000)
	public String getAttributeString() {
		return attributeString;
	}

	public void setAttributeString(String attributeString) {
		this.attributeString = attributeString;
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

	public List<Driver> getDrivers() {
		return this.drivers;
	}

	public void setDrivers(List<Driver> drivers) {
		this.drivers = drivers;
	}

	public Set<Attribute> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}
 
	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public Set<Geofence> getGeofences() {
		return this.geofences;
	}

	public void setGeofences(Set<Geofence> geofences) {
		this.geofences = geofences;
	}

	public Set<Event> getEvents() {
		return this.events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}
/*
	public Event addEvent(Event event) {
		getEvents().add(event);
		event.setDevice(this);
		return event;
	}
	public Event removeEvent(Event event) {
		getEvents().remove(event);
		event.setDevice(null);
		return event;
	}
 */
    public List<Position> getPositions() {
		return this.positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}
/*
	public Position addPosition(Position position) {
		getPositions().add(position);
		position.setDevice(this);
		return position;
	}
	public Position removePosition(Position position) {
		getPositions().remove(position);
		position.setDevice(null);
		return position;
	}
 */
	public Boolean getDisabled() {
		return this.disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public List<Command> getCommands() {
		return this.commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Device [");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (uniqueid != null) {
			builder.append("uniqueid=");
			builder.append(uniqueid);
			builder.append(", ");
		}
		if (lastupdate != null) {
			builder.append("lastupdate=");
			builder.append(lastupdate);
			builder.append(", ");
		}
		if (model != null) {
			builder.append("model=");
			builder.append(model);
			builder.append(", ");
		}
		if (group != null) {
			builder.append("group=");
			builder.append(group);
			builder.append(", ");
		}
		if (drivers != null) {
			builder.append(drivers.size() + " drivers, ");
		}
		if (attributes != null) {
			builder.append(attributes.size() + " attributes, ");
		}
		if (users != null) {
			builder.append(users.size() + " users, ");
		}
		if (geofences != null) {
			builder.append(geofences.size() + " geofences, ");
		}
		if (events != null) {
			builder.append(events.size() + " events, ");
		}
		if (positions != null) {
			builder.append(positions.size() + " positions, ");
		}
		if (commands != null) {
			builder.append(commands.size() + " commands, ");
		}
		if (maintenances != null) {
			builder.append(maintenances.size() + " maintenances, ");
		}
		builder.append(" ]");

		return builder.toString();
	}

}
