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
 * Class to hold failed login attempts. An entry is either for an IP address or
 * for a User, but not both. This way, IP lockouts and User lockouts are
 * independent, and clearing one will not affect the other. Once the number of
 * Attempts for a user or IP reaches a threshold, all Attempts are removed and a
 * LoginLockout is created. Attempts do not expire, but are cleared on
 * successful authentication. If an attempt is for a known username, two objects
 * are created, one for the IP and one for the user. If the username is unknown,
 * then only one is created for the IP.
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
@Entity
public class LoginAttempt extends DomainObject {

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
     * Returns the ID of the Attempt for Hibernate
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the attempt for Hibernate.
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the user associated ith the attempt, or null if this is an IP
     * attempt.
     *
     * @return the user
     */
    public User getUser () {
        return user;
    }

    /**
     * Sets the user associated witht he attempt
     *
     * @param user
     *            the user to set
     */
    public void setUser ( final User user ) {
        this.user = user;
    }

    /**
     * Gets the IP associated with the attempt, or null if this holds a User.
     *
     * @return the ip
     */
    public String getIp () {
        return ip;
    }

    /**
     * Sets the IP associated with the attempt
     *
     * @param ip
     *            the ip to set
     */
    public void setIp ( final String ip ) {
        this.ip = ip;
    }

    /**
     * Gets the time of the attempt
     *
     * @return the time
     */
    public ZonedDateTime getTime () {
        return time;
    }

    /**
     * Sets th etime of the attempt
     *
     * @param time
     *            the time to set
     */
    public void setTime ( final ZonedDateTime time ) {
        this.time = time;
    }

}
