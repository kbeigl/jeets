package org.jeets.model.gtfs;

public class GtfsStopTimes {

    private String stopId; // stop_id
    private String tripId; // trip_id
    private int stopSequence; // stop_sequence
    private String arrivalTime; // arrival_time
    private String departureTime; // departure_time
    private String stopHeadsign; // stop_headsign
    private int pickupType; // pickup_type
    private int dropOffType; // drop_off_type

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getStopHeadsign() {
        return stopHeadsign;
    }

    public void setStopHeadsign(String stopHeadsign) {
        this.stopHeadsign = stopHeadsign;
    }

    public int getPickupType() {
        return pickupType;
    }

    public void setPickupType(int pickupType) {
        this.pickupType = pickupType;
    }

    public int getDropOffType() {
        return dropOffType;
    }

    public void setDropOffType(int dropOffType) {
        this.dropOffType = dropOffType;
    }

    @Override
    public String toString() {
        return "GtfsStopTimes [stopId=" + stopId + ", tripId=" + tripId + ", stopSequence=" + stopSequence
                + ", arrivalTime=" + arrivalTime + ", departureTime=" + departureTime + ", stopHeadsign=" + stopHeadsign
                + ", pickupType=" + pickupType + ", dropOffType=" + dropOffType + "]";
    }

}
