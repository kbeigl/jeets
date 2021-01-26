package org.jeets.model.gtfs;
// Generated 08.11.2017 22:44:26 by Hibernate Tools 4.3.5.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/** UniversalCalendarId generated by hbm2java */
@Embeddable
public class UniversalCalendarId implements java.io.Serializable {

  private String serviceId;
  private Date date;

  public UniversalCalendarId() {}

  public UniversalCalendarId(String serviceId, Date date) {
    this.serviceId = serviceId;
    this.date = date;
  }

  @Column(name = "service_id", nullable = false)
  public String getServiceId() {
    return this.serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "date", nullable = false, length = 13)
  public Date getDate() {
    return this.date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public boolean equals(Object other) {
    if ((this == other)) return true;
    if ((other == null)) return false;
    if (!(other instanceof UniversalCalendarId)) return false;
    UniversalCalendarId castOther = (UniversalCalendarId) other;

    return ((this.getServiceId() == castOther.getServiceId())
            || (this.getServiceId() != null
                && castOther.getServiceId() != null
                && this.getServiceId().equals(castOther.getServiceId())))
        && ((this.getDate() == castOther.getDate())
            || (this.getDate() != null
                && castOther.getDate() != null
                && this.getDate().equals(castOther.getDate())));
  }

  public int hashCode() {
    int result = 17;

    result = 37 * result + (getServiceId() == null ? 0 : this.getServiceId().hashCode());
    result = 37 * result + (getDate() == null ? 0 : this.getDate().hashCode());
    return result;
  }
}
