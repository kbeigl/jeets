package org.jeets.model.gtfs;
// Generated 08.11.2017 22:44:26 by Hibernate Tools 4.3.5.Final

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/** RouteDirections generated by hbm2java */
@Entity
@Table(name = "route_directions", schema = "public")
public class RouteDirections implements java.io.Serializable {

  private RouteDirectionsId id;
  private String directionName;

  public RouteDirections() {}

  public RouteDirections(RouteDirectionsId id) {
    this.id = id;
  }

  public RouteDirections(RouteDirectionsId id, String directionName) {
    this.id = id;
    this.directionName = directionName;
  }

  @EmbeddedId
  @AttributeOverrides({
    @AttributeOverride(name = "routeId", column = @Column(name = "route_id", nullable = false)),
    @AttributeOverride(
        name = "directionId",
        column = @Column(name = "direction_id", nullable = false))
  })
  public RouteDirectionsId getId() {
    return this.id;
  }

  public void setId(RouteDirectionsId id) {
    this.id = id;
  }

  @Column(name = "direction_name")
  public String getDirectionName() {
    return this.directionName;
  }

  public void setDirectionName(String directionName) {
    this.directionName = directionName;
  }
}
