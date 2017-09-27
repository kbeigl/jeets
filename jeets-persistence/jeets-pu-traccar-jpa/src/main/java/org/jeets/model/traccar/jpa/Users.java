package org.jeets.model.traccar.jpa;
// Generated 15.09.2017 14:36:40 by Hibernate Tools 4.3.5.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Users generated by hbm2java
 */
@Entity
@Table(name = "users", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Users implements java.io.Serializable {

    private int id;
    private String name;
    private String email;
    private String hashedpassword;
    private String salt;
    private boolean readonly;
    private boolean admin;
    private String map;
    private double latitude;
    private double longitude;
    private int zoom;
    private boolean twelvehourformat;
    private String attributes;
    private String coordinateformat;
    private Boolean disabled;
    private Date expirationtime;
    private Integer devicelimit;
    private String token;
    private Integer userlimit;
    private Boolean devicereadonly;
    private String phone;
    private Set<UserGroup> userGroups = new HashSet<UserGroup>(0);
    private Set<Notifications> notificationses = new HashSet<Notifications>(0);
    private Set<UserAttribute> userAttributes = new HashSet<UserAttribute>(0);
    private Set<UserDevice> userDevices = new HashSet<UserDevice>(0);
    private Set<UserCalendar> userCalendars = new HashSet<UserCalendar>(0);
    private Set<UserGeofence> userGeofences = new HashSet<UserGeofence>(0);
    private Set<UserUser> userUsersForUserid = new HashSet<UserUser>(0);
    private Set<UserDriver> userDrivers = new HashSet<UserDriver>(0);
    private Set<UserUser> userUsersForManageduserid = new HashSet<UserUser>(0);

    public Users() {
    }

    public Users(int id, String name, String email, String hashedpassword, String salt, boolean readonly, boolean admin,
            double latitude, double longitude, int zoom, boolean twelvehourformat) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hashedpassword = hashedpassword;
        this.salt = salt;
        this.readonly = readonly;
        this.admin = admin;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
        this.twelvehourformat = twelvehourformat;
    }

    public Users(int id, String name, String email, String hashedpassword, String salt, boolean readonly, boolean admin,
            String map, double latitude, double longitude, int zoom, boolean twelvehourformat, String attributes,
            String coordinateformat, Boolean disabled, Date expirationtime, Integer devicelimit, String token,
            Integer userlimit, Boolean devicereadonly, String phone, Set<UserGroup> userGroups,
            Set<Notifications> notificationses, Set<UserAttribute> userAttributes, Set<UserDevice> userDevices,
            Set<UserCalendar> userCalendars, Set<UserGeofence> userGeofences, Set<UserUser> userUsersForUserid,
            Set<UserDriver> userDrivers, Set<UserUser> userUsersForManageduserid) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hashedpassword = hashedpassword;
        this.salt = salt;
        this.readonly = readonly;
        this.admin = admin;
        this.map = map;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
        this.twelvehourformat = twelvehourformat;
        this.attributes = attributes;
        this.coordinateformat = coordinateformat;
        this.disabled = disabled;
        this.expirationtime = expirationtime;
        this.devicelimit = devicelimit;
        this.token = token;
        this.userlimit = userlimit;
        this.devicereadonly = devicereadonly;
        this.phone = phone;
        this.userGroups = userGroups;
        this.notificationses = notificationses;
        this.userAttributes = userAttributes;
        this.userDevices = userDevices;
        this.userCalendars = userCalendars;
        this.userGeofences = userGeofences;
        this.userUsersForUserid = userUsersForUserid;
        this.userDrivers = userDrivers;
        this.userUsersForManageduserid = userUsersForManageduserid;
    }

    @Id

    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false, length = 128)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "email", unique = true, nullable = false, length = 128)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "hashedpassword", nullable = false, length = 128)
    public String getHashedpassword() {
        return this.hashedpassword;
    }

    public void setHashedpassword(String hashedpassword) {
        this.hashedpassword = hashedpassword;
    }

    @Column(name = "salt", nullable = false, length = 128)
    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Column(name = "readonly", nullable = false)
    public boolean isReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Column(name = "admin", nullable = false)
    public boolean isAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Column(name = "map", length = 128)
    public String getMap() {
        return this.map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    @Column(name = "latitude", nullable = false, precision = 17, scale = 17)
    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Column(name = "longitude", nullable = false, precision = 17, scale = 17)
    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Column(name = "zoom", nullable = false)
    public int getZoom() {
        return this.zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    @Column(name = "twelvehourformat", nullable = false)
    public boolean isTwelvehourformat() {
        return this.twelvehourformat;
    }

    public void setTwelvehourformat(boolean twelvehourformat) {
        this.twelvehourformat = twelvehourformat;
    }

    @Column(name = "attributes", length = 4000)
    public String getAttributes() {
        return this.attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    @Column(name = "coordinateformat", length = 128)
    public String getCoordinateformat() {
        return this.coordinateformat;
    }

    public void setCoordinateformat(String coordinateformat) {
        this.coordinateformat = coordinateformat;
    }

    @Column(name = "disabled")
    public Boolean getDisabled() {
        return this.disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expirationtime", length = 29)
    public Date getExpirationtime() {
        return this.expirationtime;
    }

    public void setExpirationtime(Date expirationtime) {
        this.expirationtime = expirationtime;
    }

    @Column(name = "devicelimit")
    public Integer getDevicelimit() {
        return this.devicelimit;
    }

    public void setDevicelimit(Integer devicelimit) {
        this.devicelimit = devicelimit;
    }

    @Column(name = "token", length = 128)
    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Column(name = "userlimit")
    public Integer getUserlimit() {
        return this.userlimit;
    }

    public void setUserlimit(Integer userlimit) {
        this.userlimit = userlimit;
    }

    @Column(name = "devicereadonly")
    public Boolean getDevicereadonly() {
        return this.devicereadonly;
    }

    public void setDevicereadonly(Boolean devicereadonly) {
        this.devicereadonly = devicereadonly;
    }

    @Column(name = "phone", length = 128)
    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<UserGroup> getUserGroups() {
        return this.userGroups;
    }

    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<Notifications> getNotificationses() {
        return this.notificationses;
    }

    public void setNotificationses(Set<Notifications> notificationses) {
        this.notificationses = notificationses;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<UserAttribute> getUserAttributes() {
        return this.userAttributes;
    }

    public void setUserAttributes(Set<UserAttribute> userAttributes) {
        this.userAttributes = userAttributes;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<UserDevice> getUserDevices() {
        return this.userDevices;
    }

    public void setUserDevices(Set<UserDevice> userDevices) {
        this.userDevices = userDevices;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<UserCalendar> getUserCalendars() {
        return this.userCalendars;
    }

    public void setUserCalendars(Set<UserCalendar> userCalendars) {
        this.userCalendars = userCalendars;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<UserGeofence> getUserGeofences() {
        return this.userGeofences;
    }

    public void setUserGeofences(Set<UserGeofence> userGeofences) {
        this.userGeofences = userGeofences;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "usersByUserid")
    public Set<UserUser> getUserUsersForUserid() {
        return this.userUsersForUserid;
    }

    public void setUserUsersForUserid(Set<UserUser> userUsersForUserid) {
        this.userUsersForUserid = userUsersForUserid;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<UserDriver> getUserDrivers() {
        return this.userDrivers;
    }

    public void setUserDrivers(Set<UserDriver> userDrivers) {
        this.userDrivers = userDrivers;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "usersByManageduserid")
    public Set<UserUser> getUserUsersForManageduserid() {
        return this.userUsersForManageduserid;
    }

    public void setUserUsersForManageduserid(Set<UserUser> userUsersForManageduserid) {
        this.userUsersForManageduserid = userUsersForManageduserid;
    }

}
