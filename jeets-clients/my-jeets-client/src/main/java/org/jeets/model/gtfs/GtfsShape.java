package org.jeets.model.gtfs;

public class GtfsShape {

    private String shapeId; // shape_id
    private double shapePtLat; // shape_pt_lat
    private double shapePtLon; // shape_pt_lon
    private int shapePtSequence; // shape_pt_sequence

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public double getShapePtLat() {
        return shapePtLat;
    }

    public void setShapePtLat(double shapePtLat) {
        this.shapePtLat = shapePtLat;
    }

    public double getShapePtLon() {
        return shapePtLon;
    }

    public void setShapePtLon(double shapePtLon) {
        this.shapePtLon = shapePtLon;
    }

    public int getShapePtSequence() {
        return shapePtSequence;
    }

    public void setShapePtSequence(int shapePtSequence) {
        this.shapePtSequence = shapePtSequence;
    }

    @Override
    public String toString() {
        return "GtfsShape [shapeId=" + shapeId + ", shapePtLat=" + shapePtLat + ", shapePtLon=" + shapePtLon
                + ", shapePtSequence=" + shapePtSequence + "]";
    }

}
