package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="tc_commands")
@NamedQuery(name="Command.findAll", query="SELECT c FROM Command c")
public class Command implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tc_commands_id_gen")
	@SequenceGenerator(name="tc_commands_id_gen", sequenceName="tc_commands_id_seq")
	private Integer id;
	private String attributes;
	private String description;
	private Boolean textchannel;
	private String type;

	public Command() {
	}

	@ManyToMany(mappedBy="commands")
	private List<Device> devices;

	@ManyToMany(mappedBy="commands")
	private List<Group> groups;

	@ManyToMany(mappedBy="commands")
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getTextchannel() {
		return this.textchannel;
	}

	public void setTextchannel(Boolean textchannel) {
		this.textchannel = textchannel;
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
		return "Command [id=" + id + ", attributes=" + attributes + ", description=" + description + ", textchannel="
				+ textchannel + ", type=" + type + ", devices="
				+ (devices != null ? devices.subList(0, Math.min(devices.size(), maxLen)) : null) + ", groups="
				+ (groups != null ? groups.subList(0, Math.min(groups.size(), maxLen)) : null) + ", users="
				+ (users != null ? users.subList(0, Math.min(users.size(), maxLen)) : null) + "]";
	}

}
