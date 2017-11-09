package org.jeets.model.gtfs;
// Generated 08.11.2017 22:44:26 by Hibernate Tools 4.3.5.Final

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Shapes generated by hbm2java
 */
@Entity
@Table(name = "shapes", schema = "public")
public class Shapes implements java.io.Serializable {

    private ShapesId id;
    private BigDecimal shapePtLat;
    private BigDecimal shapePtLon;
    private BigDecimal shapeDistTraveled;
    private Serializable geom;

    public Shapes() {
    }

    public Shapes(ShapesId id) {
        this.id = id;
    }

    public Shapes(ShapesId id, BigDecimal shapePtLat, BigDecimal shapePtLon, BigDecimal shapeDistTraveled,
            Serializable geom) {
        this.id = id;
        this.shapePtLat = shapePtLat;
        this.shapePtLon = shapePtLon;
        this.shapeDistTraveled = shapeDistTraveled;
        this.geom = geom;
    }

    @EmbeddedId

    @AttributeOverrides({ @AttributeOverride(name = "shapeId", column = @Column(name = "shape_id", nullable = false)),
            @AttributeOverride(name = "shapePtSequence", column = @Column(name = "shape_pt_sequence", nullable = false)) })
    public ShapesId getId() {
        return this.id;
    }

    public void setId(ShapesId id) {
        this.id = id;
    }

    @Column(name = "shape_pt_lat", precision = 12, scale = 9)
    public BigDecimal getShapePtLat() {
        return this.shapePtLat;
    }

    public void setShapePtLat(BigDecimal shapePtLat) {
        this.shapePtLat = shapePtLat;
    }

    @Column(name = "shape_pt_lon", precision = 12, scale = 9)
    public BigDecimal getShapePtLon() {
        return this.shapePtLon;
    }

    public void setShapePtLon(BigDecimal shapePtLon) {
        this.shapePtLon = shapePtLon;
    }

    @Column(name = "shape_dist_traveled", precision = 20, scale = 10)
    public BigDecimal getShapeDistTraveled() {
        return this.shapeDistTraveled;
    }

    public void setShapeDistTraveled(BigDecimal shapeDistTraveled) {
        this.shapeDistTraveled = shapeDistTraveled;
    }

    @Column(name = "geom")
    public Serializable getGeom() {
        return this.geom;
    }

    public void setGeom(Serializable geom) {
        this.geom = geom;
    }

}
