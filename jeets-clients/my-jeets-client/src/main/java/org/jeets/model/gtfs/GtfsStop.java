package org.jeets.model.gtfs;

public class GtfsStop {

    // only attributes filled by HVV are currently active
    private String stopId; // stop_id
    // stop_code character varying(50),
    private String stopName; // stop_name
    // stop_desc character varying(255),
    private double stopLat; // stop_lat
    private double stopLon; // stop_lon
    // zone_id character varying(50),
    // stop_url character varying(255),
    private double stopLocType; // location_type integer > boolean ??
    private String parentStation; // parent_station
    // stop_timezone character varying(50),
    private double wheelchair; // wheelchair_boarding integer > boolean ??
    // platform_code character varying(50),
    // direction

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public double getStopLat() {
        return stopLat;
    }

    public void setStopLat(double stopLat) {
        this.stopLat = stopLat;
    }

    public double getStopLon() {
        return stopLon;
    }

    public void setStopLon(double stopLon) {
        this.stopLon = stopLon;
    }

    public double getStopLocType() {
        return stopLocType;
    }

    public void setStopLocType(double stopLocType) {
        this.stopLocType = stopLocType;
    }

    public String getParentStation() {
        return parentStation;
    }

    public void setParentStation(String parentStation) {
        this.parentStation = parentStation;
    }

    public double getWheelchair() {
        return wheelchair;
    }

    public void setWheelchair(double wheelchair) {
        this.wheelchair = wheelchair;
    }

    @Override
    public String toString() {
        return "GtfsStop [stopId=" + stopId + ", stopName=" + stopName + ", stopLat=" + stopLat + ", stopLon=" + stopLon
                + ", stopLocType=" + stopLocType + ", parentStation=" + parentStation + ", wheelchair=" + wheelchair
                + "]";
    }

}
