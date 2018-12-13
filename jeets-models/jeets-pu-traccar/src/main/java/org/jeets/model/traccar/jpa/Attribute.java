package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="tc_attributes")
public class Attribute implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tc_attributes_id_gen")
	@SequenceGenerator(name="tc_attributes_id_gen", sequenceName="tc_attributes_id_seq")
	private Integer id;
	private String attribute;
	private String description;
	private String expression;
	private String type;

	public Attribute() {
	}

	@ManyToMany(mappedBy="attributes")
	private List<Device> devices;

	@ManyToMany(mappedBy="attributes")
	private List<Group> groups;

	@ManyToMany(mappedBy="attributes")
	private List<User> users;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAttribute() {
		return this.attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpression() {
		return this.expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
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
		return "Attribute [id=" + id + ", attribute=" + attribute + ", description=" + description + ", expression="
				+ expression + ", type=" + type + ", devices="
				+ (devices != null ? devices.subList(0, Math.min(devices.size(), maxLen)) : null) + ", groups="
				+ (groups != null ? groups.subList(0, Math.min(groups.size(), maxLen)) : null) + ", users="
				+ (users != null ? users.subList(0, Math.min(users.size(), maxLen)) : null) + "]";
	}

}
