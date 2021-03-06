package org.jeets.model.gtfs;
// Generated 08.11.2017 22:44:26 by Hibernate Tools 4.3.5.Final

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/** UniversalCalendar generated by hbm2java */
@Entity
@Table(name = "universal_calendar", schema = "public")
public class UniversalCalendar implements java.io.Serializable {

  private UniversalCalendarId id;

  public UniversalCalendar() {}

  public UniversalCalendar(UniversalCalendarId id) {
    this.id = id;
  }

  @EmbeddedId
  @AttributeOverrides({
    @AttributeOverride(name = "serviceId", column = @Column(name = "service_id", nullable = false)),
    @AttributeOverride(
        name = "date",
        column = @Column(name = "date", nullable = false, length = 13))
  })
  public UniversalCalendarId getId() {
    return this.id;
  }

  public void setId(UniversalCalendarId id) {
    this.id = id;
  }
}
