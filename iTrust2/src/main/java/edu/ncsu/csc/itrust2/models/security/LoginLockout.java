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
 * Class that holds a lockout for a user or ip. It contains a timestamp used to
 * determine if the lockout is still valid. 3 Lockouts within a 24-hour period
 * result in a LoginBan. Upon the elevation to a LoginBan, all associated
 * LoginLockout objects are deleted.
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
@Entity
public class LoginLockout extends DomainObject {

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
     * Retunrns the ID of the LoginLockout for hibernate.
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID. For hibernate
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the user associated with the lockout, or null if this is an IP
     * lockout.
     *
     * @return the user
     */
    public User getUser () {
        return user;
    }

    /**
     * Sets the user associated with the lockout.
     *
     * @param user
     *            the user to set
     */
    public void setUser ( final User user ) {
        this.user = user;
    }

    /**
     * Returns the IP associated with the lockout, or null if this is a User
     * lockout.
     *
     * @return the ip
     */
    public String getIp () {
        return ip;
    }

    /**
     * Sets the IP associated with the lockout
     *
     * @param ip
     *            the ip to set
     */
    public void setIp ( final String ip ) {
        this.ip = ip;
    }

    /**
     * Returns the starting time of the lockout.
     *
     * @return the time
     */
    public ZonedDateTime getTime () {
        return time;
    }

    /**
     * Sets the starting time of the lockout
     *
     * @param time
     *            the time to set
     */
    public void setTime ( final ZonedDateTime time ) {
        this.time = time;
    }

}
