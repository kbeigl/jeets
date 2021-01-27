package org.jeets.model.traccar.jpa;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "tc_positions")
public class Position implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tc_positions_id_gen")
  @SequenceGenerator(
      name = "tc_positions_id_gen",
      sequenceName = "tc_positions_id_seq",
      allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Integer id;

  private String protocol;
  private Date servertime;
  private Date devicetime;
  private Date fixtime;
  private Boolean valid;
  private double latitude;
  private double longitude;
  private double altitude;
  private double speed;
  private double course;
  private String address;
  private String attributes;
  private double accuracy;
  private String network;

  public Position() {}

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
  @JoinColumn(name = "deviceid") // , nullable = false)
  private Device device;

  public Integer getId() {
    return this.id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Device getDevice() {
    return this.device;
  }

  public void setDevice(Device device) {
    this.device = device;
  }

  @Column(name = "protocol", length = 128)
  public String getProtocol() {
    return this.protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "servertime", nullable = false, length = 29)
  public Date getServertime() {
    return this.servertime;
  }

  public void setServertime(Date servertime) {
    this.servertime = servertime;
  }

  /** Time when message was sent by client. */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "devicetime", nullable = false, length = 29)
  public Date getDevicetime() {
    return this.devicetime;
  }

  /**
   * Time when message was sent by client.
   *
   * @param devicetime
   */
  public void setDevicetime(Date devicetime) {
    this.devicetime = devicetime;
  }

  /** Time when GPS position(lat,lon) was fixed by GPS unit. */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "fixtime", nullable = false, length = 29)
  public Date getFixtime() {
    return this.fixtime;
  }

  /**
   * Time when GPS position(lat,lon) was fixed by GPS unit.
   *
   * @param fixtime
   */
  public void setFixtime(Date fixtime) {
    this.fixtime = fixtime;
  }

  @Column(name = "valid", nullable = false)
  public Boolean getValid() {
    return this.valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
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

  @Column(name = "altitude", nullable = false, precision = 17, scale = 17)
  public double getAltitude() {
    return this.altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }

  @Column(name = "speed", nullable = false, precision = 17, scale = 17)
  public double getSpeed() {
    return this.speed;
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }

  @Column(name = "course", nullable = false, precision = 17, scale = 17)
  public double getCourse() {
    return this.course;
  }

  public void setCourse(double course) {
    this.course = course;
  }

  @Column(name = "address", length = 512)
  public String getAddress() {
    return this.address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Column(name = "attributes", length = 4000)
  public String getAttributes() {
    return this.attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  @Column(name = "accuracy", nullable = false, precision = 17, scale = 17)
  public double getAccuracy() {
    return this.accuracy;
  }

  public void setAccuracy(double accuracy) {
    this.accuracy = accuracy;
  }

  @Column(name = "network", length = 4000)
  public String getNetwork() {
    return this.network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  @Override
  public String toString() {
    return "Position [id="
        + id
        + ", device="
        + device.getName()
        + "("
        + device.getUniqueid()
        + ", protocol="
        + protocol
        + ", "
        //				+ "servertime=" + servertime + ", devicetime=" + devicetime + ", "
        + "fixtime="
        + fixtime
        + ", valid="
        + valid
        + ", latitude="
        + latitude
        + ", longitude="
        + longitude
        + ", speed="
        + speed
        + ", course="
        + course
        + ", address="
        + address
        //				+ ", attributes=" + attributes + ", accuracy=" + accuracy + ", network=" + network
        + "]";
  }
}
