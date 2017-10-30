package org.jeets.model.gtfs;

public class GtfsRoute {

    private String routeId = null;
    private String agencyId = null;
    private String routeShortName = null;
    private String routeLongName = null;
    // route_desc character varying(1023),
    // route_type integer NOT NULL,
    // route_url character varying(255),
    // route_color character varying(6),
    // route_text_color character varying(6),
    // route_sort_order integer,
    // min_headway_minutes integer

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    @Override
    public String toString() {
        return "GtfsRoute [routeId=" + routeId + ", agencyId=" + agencyId + ", routeShortName=" + routeShortName
                + ", routeLongName=" + routeLongName + "]";
    }

}
