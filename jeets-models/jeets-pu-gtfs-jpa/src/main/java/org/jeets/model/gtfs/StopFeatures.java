package org.jeets.model.gtfs;
// Generated 08.11.2017 22:44:26 by Hibernate Tools 4.3.5.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * StopFeatures generated by hbm2java
 */
@Entity
@Table(name = "stop_features", schema = "public")
public class StopFeatures implements java.io.Serializable {

    private int id;
    private String stopId;
    private String featureType;
    private String featureName;

    public StopFeatures() {
    }

    public StopFeatures(int id, String stopId, String featureType) {
        this.id = id;
        this.stopId = stopId;
        this.featureType = featureType;
    }

    public StopFeatures(int id, String stopId, String featureType, String featureName) {
        this.id = id;
        this.stopId = stopId;
        this.featureType = featureType;
        this.featureName = featureName;
    }

    @Id

    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "stop_id", nullable = false)
    public String getStopId() {
        return this.stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    @Column(name = "feature_type", nullable = false, length = 50)
    public String getFeatureType() {
        return this.featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    @Column(name = "feature_name")
    public String getFeatureName() {
        return this.featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

}