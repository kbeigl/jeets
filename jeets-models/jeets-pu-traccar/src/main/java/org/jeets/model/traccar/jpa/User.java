package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name="tc_users")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="tc_users_id_gen", sequenceName="tc_users_id_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tc_users_id_gen")
	private Integer id;

	private Boolean administrator;
	private String attributeString;
	private String coordinateformat;
	private Integer devicelimit;
	private Boolean devicereadonly;
	private Boolean disabled;
	private String email;
	private Timestamp expirationtime;
	private String hashedpassword;
	private double latitude;
	private Boolean limitcommands;
	private String login;
	private double longitude;
	private String map;
	private String name;
	private String phone;
	private String poilayer;
	private Boolean readonly;
	private String salt;
	private String token;
	private Boolean twelvehourformat;
	private Integer userlimit;
	private Integer zoom;

	public User() {
	}

	@ManyToMany
	@JoinTable(name = "tc_user_attribute", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "attributeid") })
	private List<Attribute> attributes;

	@ManyToMany
	@JoinTable(name = "tc_user_calendar", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "calendarid") })
	private List<Calendar> calendars;

	@ManyToMany
	@JoinTable(name = "tc_user_command", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "commandid") })
	private List<Command> commands;

	@ManyToMany
	@JoinTable(name = "tc_user_device", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "deviceid") })
	private List<Device> devices;

	@ManyToMany
	@JoinTable(name = "tc_user_driver", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "driverid") })
	private List<Driver> drivers;

	@ManyToMany
	@JoinTable(name = "tc_user_geofence", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "geofenceid") })
	private List<Geofence> geofences;

	@ManyToMany
	@JoinTable(name = "tc_user_group", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "groupid") })
	private List<Group> groups;

	@ManyToMany
	@JoinTable(name = "tc_user_maintenance", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "maintenanceid") })
	private List<Maintenance> maintenances;

	@ManyToMany
	@JoinTable(name = "tc_user_notification", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "notificationid") })
	private List<Notification> notifications;

	@ManyToMany
	@JoinTable(name = "tc_user_user", 
		joinColumns = { @JoinColumn(name = "userid") }, 
		inverseJoinColumns = { @JoinColumn(name = "manageduserid") })
	private List<User> users1;

	@ManyToMany(mappedBy="users1")
	private List<User> users2;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getAdministrator() {
		return this.administrator;
	}

	public void setAdministrator(Boolean administrator) {
		this.administrator = administrator;
	}

	public String getAttributeString() {
		return attributeString;
	}

	public void setAttributeString(String attributeString) {
		this.attributeString = attributeString;
	}

	public String getCoordinateformat() {
		return this.coordinateformat;
	}

	public void setCoordinateformat(String coordinateformat) {
		this.coordinateformat = coordinateformat;
	}

	public Integer getDevicelimit() {
		return this.devicelimit;
	}

	public void setDevicelimit(Integer devicelimit) {
		this.devicelimit = devicelimit;
	}

	public Boolean getDevicereadonly() {
		return this.devicereadonly;
	}

	public void setDevicereadonly(Boolean devicereadonly) {
		this.devicereadonly = devicereadonly;
	}

	public Boolean getDisabled() {
		return this.disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getExpirationtime() {
		return this.expirationtime;
	}

	public void setExpirationtime(Timestamp expirationtime) {
		this.expirationtime = expirationtime;
	}

	public String getHashedpassword() {
		return this.hashedpassword;
	}

	public void setHashedpassword(String hashedpassword) {
		this.hashedpassword = hashedpassword;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public Boolean getLimitcommands() {
		return this.limitcommands;
	}

	public void setLimitcommands(Boolean limitcommands) {
		this.limitcommands = limitcommands;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getMap() {
		return this.map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPoilayer() {
		return this.poilayer;
	}

	public void setPoilayer(String poilayer) {
		this.poilayer = poilayer;
	}

	public Boolean getReadonly() {
		return this.readonly;
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	public String getSalt() {
		return this.salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Boolean getTwelvehourformat() {
		return this.twelvehourformat;
	}

	public void setTwelvehourformat(Boolean twelvehourformat) {
		this.twelvehourformat = twelvehourformat;
	}

	public Integer getUserlimit() {
		return this.userlimit;
	}

	public void setUserlimit(Integer userlimit) {
		this.userlimit = userlimit;
	}

	public Integer getZoom() {
		return this.zoom;
	}

	public void setZoom(Integer zoom) {
		this.zoom = zoom;
	}

	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public List<Calendar> getCalendars() {
		return this.calendars;
	}

	public void setCalendars(List<Calendar> calendars) {
		this.calendars = calendars;
	}

	public List<Command> getCommands() {
		return this.commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public List<Device> getDevices() {
		return this.devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
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

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
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

	public List<User> getUsers1() {
		return this.users1;
	}

	public void setUsers1(List<User> users1) {
		this.users1 = users1;
	}

	public List<User> getUsers2() {
		return this.users2;
	}

	public void setUsers2(List<User> users2) {
		this.users2 = users2;
	}

	@Override
	public String toString() {
		final int maxLen = 3;
		return "User [id=" + id + ", administrator=" + administrator + ", attributeString=" + attributeString
				+ ", coordinateformat=" + coordinateformat + ", devicelimit=" + devicelimit + ", devicereadonly="
				+ devicereadonly + ", disabled=" + disabled + ", email=" + email + ", expirationtime=" + expirationtime
				+ ", hashedpassword=" + hashedpassword + ", latitude=" + latitude + ", limitcommands=" + limitcommands
				+ ", login=" + login + ", longitude=" + longitude + ", map=" + map + ", name=" + name + ", phone="
				+ phone + ", poilayer=" + poilayer + ", readonly=" + readonly + ", salt=" + salt + ", token=" + token
				+ ", twelvehourformat=" + twelvehourformat + ", userlimit=" + userlimit + ", zoom=" + zoom
				+ ", attributes="
				+ (attributes != null ? attributes.subList(0, Math.min(attributes.size(), maxLen)) : null)
				+ ", calendars=" + (calendars != null ? calendars.subList(0, Math.min(calendars.size(), maxLen)) : null)
				+ ", commands=" + (commands != null ? commands.subList(0, Math.min(commands.size(), maxLen)) : null)
				+ ", devices=" + (devices != null ? devices.subList(0, Math.min(devices.size(), maxLen)) : null)
				+ ", drivers=" + (drivers != null ? drivers.subList(0, Math.min(drivers.size(), maxLen)) : null)
				+ ", geofences=" + (geofences != null ? geofences.subList(0, Math.min(geofences.size(), maxLen)) : null)
				+ ", groups=" + (groups != null ? groups.subList(0, Math.min(groups.size(), maxLen)) : null)
				+ ", maintenances="
				+ (maintenances != null ? maintenances.subList(0, Math.min(maintenances.size(), maxLen)) : null)
				+ ", notifications="
				+ (notifications != null ? notifications.subList(0, Math.min(notifications.size(), maxLen)) : null)
				+ ", users1=" + (users1 != null ? users1.subList(0, Math.min(users1.size(), maxLen)) : null)
				+ ", users2=" + (users2 != null ? users2.subList(0, Math.min(users2.size(), maxLen)) : null) + "]";
	}

}
