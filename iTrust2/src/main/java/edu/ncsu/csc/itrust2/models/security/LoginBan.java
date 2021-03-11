package edu.ncsu.csc.iTrust2.models.security;

import java.time.ZonedDateTime;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAdapter;
import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAttributeConverter;
import edu.ncsu.csc.iTrust2.models.DomainObject;
import edu.ncsu.csc.iTrust2.models.User;

/**
 * Contains info about a LoginBan from the system. A ban does not expire, and
 * can only be removed by an admin (Not Implemented). A ban can be for either a
 * User or an IP.
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
@Entity
public class LoginBan extends DomainObject {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long          id;

    private String        ip;

    @ManyToOne
    @JoinColumn ( name = "user_id", columnDefinition = "varchar(100)" )
    private User          user;

    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = ZonedDateTimeAttributeConverter.class )
    @JsonAdapter ( ZonedDateTimeAdapter.class )
    private ZonedDateTime time;

    /**
     * Returns the ID of the LoginBan for Hibernate
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the LoginBan.
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the User associated with the ban, or null if it is an IP ban.
     *
     * @return the user
     */
    public User getUser () {
        return user;
    }

    /**
     * Sets the User associated with the ban.
     *
     * @param user
     *            the user to set
     */
    public void setUser ( final User user ) {
        this.user = user;
    }

    /**
     * Gets the IP associatd with the ban, or null if it is a User ban.
     *
     * @return the ip
     */
    public String getIp () {
        return ip;
    }

    /**
     * Sets the IP to ban.
     *
     * @param ip
     *            the ip to set
     */
    public void setIp ( final String ip ) {
        this.ip = ip;
    }

    /**
     * Returns the start time of the ban.
     *
     * @return the time
     */
    public ZonedDateTime getTime () {
        return time;
    }

    /**
     * Sets the start time of the ban.
     *
     * @param time
     *            the time to set
     */
    public void setTime ( final ZonedDateTime time ) {
        this.time = time;
    }

}
