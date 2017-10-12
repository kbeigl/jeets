package org.jeets.model.traccar.jpa;
// Generated 15.09.2017 14:36:40 by Hibernate Tools 4.3.5.Final

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * UserCalendar generated by hbm2java
 */
@Entity
@Table(name = "user_calendar", schema = "public")
public class UserCalendar implements java.io.Serializable {

    private UserCalendarId id;
    private Calendars calendars;
    private Users users;

    public UserCalendar() {
    }

    public UserCalendar(UserCalendarId id, Calendars calendars, Users users) {
        this.id = id;
        this.calendars = calendars;
        this.users = users;
    }

    @EmbeddedId

    @AttributeOverrides({ @AttributeOverride(name = "userid", column = @Column(name = "userid", nullable = false)),
            @AttributeOverride(name = "calendarid", column = @Column(name = "calendarid", nullable = false)) })
    public UserCalendarId getId() {
        return this.id;
    }

    public void setId(UserCalendarId id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendarid", nullable = false, insertable = false, updatable = false)
    public Calendars getCalendars() {
        return this.calendars;
    }

    public void setCalendars(Calendars calendars) {
        this.calendars = calendars;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false, insertable = false, updatable = false)
    public Users getUsers() {
        return this.users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

}