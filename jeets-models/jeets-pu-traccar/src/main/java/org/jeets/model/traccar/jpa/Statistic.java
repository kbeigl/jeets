package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.*;

@Entity
@Table(name = "tc_statistics")
public class Statistic implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tc_statistics_id_gen")
  @SequenceGenerator(name = "tc_statistics_id_gen", sequenceName = "tc_statistics_id_seq")
  private Integer id;

  private Integer activedevices;
  private Integer activeusers;
  private String attributes;
  private Timestamp capturetime;
  private Integer geocoderrequests;
  private Integer geolocationrequests;
  private Integer mailsent;
  private Integer messagesreceived;
  private Integer messagesstored;
  private Integer requests;
  private Integer smssent;

  public Statistic() {}

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getActivedevices() {
    return this.activedevices;
  }

  public void setActivedevices(Integer activedevices) {
    this.activedevices = activedevices;
  }

  public Integer getActiveusers() {
    return this.activeusers;
  }

  public void setActiveusers(Integer activeusers) {
    this.activeusers = activeusers;
  }

  public String getAttributes() {
    return this.attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  public Timestamp getCapturetime() {
    return this.capturetime;
  }

  public void setCapturetime(Timestamp capturetime) {
    this.capturetime = capturetime;
  }

  public Integer getGeocoderrequests() {
    return this.geocoderrequests;
  }

  public void setGeocoderrequests(Integer geocoderrequests) {
    this.geocoderrequests = geocoderrequests;
  }

  public Integer getGeolocationrequests() {
    return this.geolocationrequests;
  }

  public void setGeolocationrequests(Integer geolocationrequests) {
    this.geolocationrequests = geolocationrequests;
  }

  public Integer getMailsent() {
    return this.mailsent;
  }

  public void setMailsent(Integer mailsent) {
    this.mailsent = mailsent;
  }

  public Integer getMessagesreceived() {
    return this.messagesreceived;
  }

  public void setMessagesreceived(Integer messagesreceived) {
    this.messagesreceived = messagesreceived;
  }

  public Integer getMessagesstored() {
    return this.messagesstored;
  }

  public void setMessagesstored(Integer messagesstored) {
    this.messagesstored = messagesstored;
  }

  public Integer getRequests() {
    return this.requests;
  }

  public void setRequests(Integer requests) {
    this.requests = requests;
  }

  public Integer getSmssent() {
    return this.smssent;
  }

  public void setSmssent(Integer smssent) {
    this.smssent = smssent;
  }

  @Override
  public String toString() {
    return "Statistic [id="
        + id
        + ", activedevices="
        + activedevices
        + ", activeusers="
        + activeusers
        + ", attributes="
        + attributes
        + ", capturetime="
        + capturetime
        + ", geocoderrequests="
        + geocoderrequests
        + ", geolocationrequests="
        + geolocationrequests
        + ", mailsent="
        + mailsent
        + ", messagesreceived="
        + messagesreceived
        + ", messagesstored="
        + messagesstored
        + ", requests="
        + requests
        + ", smssent="
        + smssent
        + "]";
  }
}
