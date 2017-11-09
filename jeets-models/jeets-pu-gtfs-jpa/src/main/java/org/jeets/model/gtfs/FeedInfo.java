package org.jeets.model.gtfs;
// Generated 08.11.2017 22:44:26 by Hibernate Tools 4.3.5.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * FeedInfo generated by hbm2java
 */
@Entity
@Table(name = "feed_info", schema = "public")
public class FeedInfo implements java.io.Serializable {

    private String feedPublisherName;
    private String feedPublisherUrl;
    private String feedLang;
    private Date feedStartDate;
    private Date feedEndDate;
    private String feedVersion;
    private String feedLicense;

    public FeedInfo() {
    }

    public FeedInfo(String feedPublisherName, String feedPublisherUrl, String feedLang) {
        this.feedPublisherName = feedPublisherName;
        this.feedPublisherUrl = feedPublisherUrl;
        this.feedLang = feedLang;
    }

    public FeedInfo(String feedPublisherName, String feedPublisherUrl, String feedLang, Date feedStartDate,
            Date feedEndDate, String feedVersion, String feedLicense) {
        this.feedPublisherName = feedPublisherName;
        this.feedPublisherUrl = feedPublisherUrl;
        this.feedLang = feedLang;
        this.feedStartDate = feedStartDate;
        this.feedEndDate = feedEndDate;
        this.feedVersion = feedVersion;
        this.feedLicense = feedLicense;
    }

    @Id

    @Column(name = "feed_publisher_name", unique = true, nullable = false)
    public String getFeedPublisherName() {
        return this.feedPublisherName;
    }

    public void setFeedPublisherName(String feedPublisherName) {
        this.feedPublisherName = feedPublisherName;
    }

    @Column(name = "feed_publisher_url", nullable = false)
    public String getFeedPublisherUrl() {
        return this.feedPublisherUrl;
    }

    public void setFeedPublisherUrl(String feedPublisherUrl) {
        this.feedPublisherUrl = feedPublisherUrl;
    }

    @Column(name = "feed_lang", nullable = false)
    public String getFeedLang() {
        return this.feedLang;
    }

    public void setFeedLang(String feedLang) {
        this.feedLang = feedLang;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "feed_start_date", length = 13)
    public Date getFeedStartDate() {
        return this.feedStartDate;
    }

    public void setFeedStartDate(Date feedStartDate) {
        this.feedStartDate = feedStartDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "feed_end_date", length = 13)
    public Date getFeedEndDate() {
        return this.feedEndDate;
    }

    public void setFeedEndDate(Date feedEndDate) {
        this.feedEndDate = feedEndDate;
    }

    @Column(name = "feed_version")
    public String getFeedVersion() {
        return this.feedVersion;
    }

    public void setFeedVersion(String feedVersion) {
        this.feedVersion = feedVersion;
    }

    @Column(name = "feed_license")
    public String getFeedLicense() {
        return this.feedLicense;
    }

    public void setFeedLicense(String feedLicense) {
        this.feedLicense = feedLicense;
    }

}
