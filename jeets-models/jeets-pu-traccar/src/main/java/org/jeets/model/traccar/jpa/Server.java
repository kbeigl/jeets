package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "tc_servers")
@NamedQuery(name = "Server.findAll", query = "SELECT s FROM Server s")
public class Server implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tc_servers_id_gen")
  @SequenceGenerator(name = "tc_servers_id_gen", sequenceName = "tc_servers_id_seq")
  private Integer id;

  private String attributes;
  private String bingkey;
  private String coordinateformat;
  private Boolean devicereadonly;
  private Boolean forcesettings;
  private double latitude;
  private Boolean limitcommands;
  private double longitude;
  private String map;
  private String mapurl;
  private String poilayer;
  private Boolean readonly;
  private Boolean registration;
  private Boolean twelvehourformat;
  private Integer zoom;

  public Server() {}

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

  public String getBingkey() {
    return this.bingkey;
  }

  public void setBingkey(String bingkey) {
    this.bingkey = bingkey;
  }

  public String getCoordinateformat() {
    return this.coordinateformat;
  }

  public void setCoordinateformat(String coordinateformat) {
    this.coordinateformat = coordinateformat;
  }

  public Boolean getDevicereadonly() {
    return this.devicereadonly;
  }

  public void setDevicereadonly(Boolean devicereadonly) {
    this.devicereadonly = devicereadonly;
  }

  public Boolean getForcesettings() {
    return this.forcesettings;
  }

  public void setForcesettings(Boolean forcesettings) {
    this.forcesettings = forcesettings;
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

  public String getMapurl() {
    return this.mapurl;
  }

  public void setMapurl(String mapurl) {
    this.mapurl = mapurl;
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

  public Boolean getRegistration() {
    return this.registration;
  }

  public void setRegistration(Boolean registration) {
    this.registration = registration;
  }

  public Boolean getTwelvehourformat() {
    return this.twelvehourformat;
  }

  public void setTwelvehourformat(Boolean twelvehourformat) {
    this.twelvehourformat = twelvehourformat;
  }

  public Integer getZoom() {
    return this.zoom;
  }

  public void setZoom(Integer zoom) {
    this.zoom = zoom;
  }

  @Override
  public String toString() {
    return "Server [id="
        + id
        + ", attributes="
        + attributes
        + ", bingkey="
        + bingkey
        + ", coordinateformat="
        + coordinateformat
        + ", devicereadonly="
        + devicereadonly
        + ", forcesettings="
        + forcesettings
        + ", latitude="
        + latitude
        + ", limitcommands="
        + limitcommands
        + ", longitude="
        + longitude
        + ", map="
        + map
        + ", mapurl="
        + mapurl
        + ", poilayer="
        + poilayer
        + ", readonly="
        + readonly
        + ", registration="
        + registration
        + ", twelvehourformat="
        + twelvehourformat
        + ", zoom="
        + zoom
        + "]";
  }
}
