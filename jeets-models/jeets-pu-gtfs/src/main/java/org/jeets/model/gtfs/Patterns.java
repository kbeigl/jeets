package org.jeets.model.gtfs;
// Generated 08.11.2017 22:44:26 by Hibernate Tools 4.3.5.Final

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** Patterns generated by hbm2java */
@Entity
@Table(name = "patterns", schema = "public")
public class Patterns implements java.io.Serializable {

  private String shapeId;
  private BigDecimal patternDist;
  private Serializable geom;

  public Patterns() {}

  public Patterns(String shapeId) {
    this.shapeId = shapeId;
  }

  public Patterns(String shapeId, BigDecimal patternDist, Serializable geom) {
    this.shapeId = shapeId;
    this.patternDist = patternDist;
    this.geom = geom;
  }

  @Id
  @Column(name = "shape_id", unique = true, nullable = false)
  public String getShapeId() {
    return this.shapeId;
  }

  public void setShapeId(String shapeId) {
    this.shapeId = shapeId;
  }

  @Column(name = "pattern_dist", precision = 20, scale = 10)
  public BigDecimal getPatternDist() {
    return this.patternDist;
  }

  public void setPatternDist(BigDecimal patternDist) {
    this.patternDist = patternDist;
  }

  @Column(name = "geom")
  public Serializable getGeom() {
    return this.geom;
  }

  public void setGeom(Serializable geom) {
    this.geom = geom;
  }
}
