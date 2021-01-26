package org.jeets.model.gtfs;
// Generated 08.11.2017 22:44:26 by Hibernate Tools 4.3.5.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** FareRules generated by hbm2java */
@Entity
@Table(name = "fare_rules", schema = "public")
public class FareRules implements java.io.Serializable {

  private int id;
  private String fareId;
  private String routeId;
  private String originId;
  private String destinationId;
  private String containsId;
  private String serviceId;

  public FareRules() {}

  public FareRules(int id, String fareId) {
    this.id = id;
    this.fareId = fareId;
  }

  public FareRules(
      int id,
      String fareId,
      String routeId,
      String originId,
      String destinationId,
      String containsId,
      String serviceId) {
    this.id = id;
    this.fareId = fareId;
    this.routeId = routeId;
    this.originId = originId;
    this.destinationId = destinationId;
    this.containsId = containsId;
    this.serviceId = serviceId;
  }

  @Id
  @Column(name = "id", unique = true, nullable = false)
  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Column(name = "fare_id", nullable = false)
  public String getFareId() {
    return this.fareId;
  }

  public void setFareId(String fareId) {
    this.fareId = fareId;
  }

  @Column(name = "route_id")
  public String getRouteId() {
    return this.routeId;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  @Column(name = "origin_id")
  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(String originId) {
    this.originId = originId;
  }

  @Column(name = "destination_id")
  public String getDestinationId() {
    return this.destinationId;
  }

  public void setDestinationId(String destinationId) {
    this.destinationId = destinationId;
  }

  @Column(name = "contains_id")
  public String getContainsId() {
    return this.containsId;
  }

  public void setContainsId(String containsId) {
    this.containsId = containsId;
  }

  @Column(name = "service_id")
  public String getServiceId() {
    return this.serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }
}
