package org.jeets.model.gtfs;

/**
 * Entity to hold all Ids provided by a trip. Departure- and Arrival Stops do
 * not necessarily cover complete trip and can be 'via' stops.
 * 
 * @author kbeigl@jeets.org
 */
public class GtfsTrip {

    private String tripId = null; 
    private String routeId = null;
    private String serviceId = null; 
//  int 0,1 to boolean true,false?
    private int directionId;
//  block_id
    private String shapeId = null;
    private String tripType = null;
    private String headsign = null;
//  trip_short_name
//  bikes_allowed
//  wheelchair_accessible
    
//  these (would) belong to GtfsStopTimes =======
//  ============== to be re/moved ===============
    // departure
    private String depStop = null;
    private String depTime = null;
    private int depSeq = -1;
    // arrival
    private String arrStop = null; 
    private String arrTime = null;
    private int arrSeq = -1;
//  =============================================

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getDirectionId() {
        return directionId;
    }

    public void setDirectionId(int directionId) {
        this.directionId = directionId;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getDepStop() {
        return depStop;
    }

    public void setDepStop(String depStop) {
        this.depStop = depStop;
    }

    public String getDepTime() {
        return depTime;
    }

    public void setDepTime(String depTime) {
        this.depTime = depTime;
    }

    public int getDepSeq() {
        return depSeq;
    }

    public void setDepSeq(int depSeq) {
        this.depSeq = depSeq;
    }

    public String getArrStop() {
        return arrStop;
    }

    public void setArrStop(String arrStop) {
        this.arrStop = arrStop;
    }

    public String getArrTime() {
        return arrTime;
    }

    public void setArrTime(String arrTime) {
        this.arrTime = arrTime;
    }

    public int getArrSeq() {
        return arrSeq;
    }

    public void setArrSeq(int arrSeq) {
        this.arrSeq = arrSeq;
    }

    @Override
    public String toString() {
        return "GtfsTrip [tripId=" + tripId + ", routeId=" + routeId + ", serviceId=" + serviceId + ", directionId="
                + directionId + ", shapeId=" + shapeId + ", tripType=" + tripType + ", headsign=" + headsign
//              + ", depStop=" + depStop + ", depTime=" + depTime + ", depSeq=" + depSeq + ", arrStop=" + arrStop
//              + ", arrTime=" + arrTime + ", arrSeq=" + arrSeq 
                + "]";
    }

}
