package org.jeets.model.gtfs;

public class GtfsAgency {

    private String agencyId = null;
    private String agencyName = null;
    // agency_url character varying(255) NOT NULL,
    private String agencyTimezone = null;
    private String agencyLanguage = null;
    // agency_phone character varying(50),
    // agency_fare_url character varying(255),

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyTimezone() {
        return agencyTimezone;
    }

    public void setAgencyTimezone(String agencyTimezone) {
        this.agencyTimezone = agencyTimezone;
    }

    public String getAgencyLanguage() {
        return agencyLanguage;
    }

    public void setAgencyLanguage(String agencyLanguage) {
        this.agencyLanguage = agencyLanguage;
    }

    @Override
    public String toString() {
        return "GtfsAgency [agencyId=" + agencyId + ", agencyName=" + agencyName + ", agencyTimezone=" + agencyTimezone
                + ", agencyLanguage=" + agencyLanguage + "]";
    }

}
