package org.jeets.model.traccar.hibernate;
// Generated 26.11.2016 13:04:29 by Hibernate Tools 5.2.0.Beta1

/**
 * GroupGeofence generated by hbm2java
 */
public class GroupGeofence implements java.io.Serializable {

	private GroupGeofenceId id;
	private Geofences geofences;
	private Groups groups;

	public GroupGeofence() {
	}

	public GroupGeofence(GroupGeofenceId id, Geofences geofences, Groups groups) {
		this.id = id;
		this.geofences = geofences;
		this.groups = groups;
	}

	public GroupGeofenceId getId() {
		return this.id;
	}

	public void setId(GroupGeofenceId id) {
		this.id = id;
	}

	public Geofences getGeofences() {
		return this.geofences;
	}

	public void setGeofences(Geofences geofences) {
		this.geofences = geofences;
	}

	public Groups getGroups() {
		return this.groups;
	}

	public void setGroups(Groups groups) {
		this.groups = groups;
	}

}
