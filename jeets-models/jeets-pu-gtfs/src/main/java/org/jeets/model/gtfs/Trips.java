package org.jeets.model.gtfs;
// Generated 08.11.2017 22:44:26 by Hibernate Tools 4.3.5.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** Trips generated by hbm2java */
@Entity
@Table(name = "trips", schema = "public")
public class Trips implements java.io.Serializable {

  private String tripId;
  private String routeId;
  private String serviceId;
  private Integer directionId;
  private String blockId;
  private String shapeId;
  private String tripType;
  private String tripHeadsign;
  private String tripShortName;
  private Integer bikesAllowed;
  private Integer wheelchairAccessible;

  public Trips() {}

  public Trips(String tripId, String routeId, String serviceId) {
    this.tripId = tripId;
    this.routeId = routeId;
    this.serviceId = serviceId;
  }

  public Trips(
      String tripId,
      String routeId,
      String serviceId,
      Integer directionId,
      String blockId,
      String shapeId,
      String tripType,
      String tripHeadsign,
      String tripShortName,
      Integer bikesAllowed,
      Integer wheelchairAccessible) {
    this.tripId = tripId;
    this.routeId = routeId;
    this.serviceId = serviceId;
    this.directionId = directionId;
    this.blockId = blockId;
    this.shapeId = shapeId;
    this.tripType = tripType;
    this.tripHeadsign = tripHeadsign;
    this.tripShortName = tripShortName;
    this.bikesAllowed = bikesAllowed;
    this.wheelchairAccessible = wheelchairAccessible;
  }

  @Id
  @Column(name = "trip_id", unique = true, nullable = false)
  public String getTripId() {
    return this.tripId;
  }

  public void setTripId(String tripId) {
    this.tripId = tripId;
  }

  @Column(name = "route_id", nullable = false)
  public String getRouteId() {
    return this.routeId;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  @Column(name = "service_id", nullable = false)
  public String getServiceId() {
    return this.serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "direction_id")
  public Integer getDirectionId() {
    return this.directionId;
  }

  public void setDirectionId(Integer directionId) {
    this.directionId = directionId;
  }

  @Column(name = "block_id")
  public String getBlockId() {
    return this.blockId;
  }

  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  @Column(name = "shape_id")
  public String getShapeId() {
    return this.shapeId;
  }

  public void setShapeId(String shapeId) {
    this.shapeId = shapeId;
  }

  @Column(name = "trip_type")
  public String getTripType() {
    return this.tripType;
  }

  public void setTripType(String tripType) {
    this.tripType = tripType;
  }

  @Column(name = "trip_headsign")
  public String getTripHeadsign() {
    return this.tripHeadsign;
  }

  public void setTripHeadsign(String tripHeadsign) {
    this.tripHeadsign = tripHeadsign;
  }

  @Column(name = "trip_short_name")
  public String getTripShortName() {
    return this.tripShortName;
  }

  public void setTripShortName(String tripShortName) {
    this.tripShortName = tripShortName;
  }

  @Column(name = "bikes_allowed")
  public Integer getBikesAllowed() {
    return this.bikesAllowed;
  }

  public void setBikesAllowed(Integer bikesAllowed) {
    this.bikesAllowed = bikesAllowed;
  }

  @Column(name = "wheelchair_accessible")
  public Integer getWheelchairAccessible() {
    return this.wheelchairAccessible;
  }

  public void setWheelchairAccessible(Integer wheelchairAccessible) {
    this.wheelchairAccessible = wheelchairAccessible;
  }
}
