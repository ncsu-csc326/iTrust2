package edu.ncsu.csc.itrust2.models.persistent;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.annotations.JsonAdapter;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.adapters.ZonedDateTimeAdapter;
import edu.ncsu.csc.itrust2.adapters.ZonedDateTimeAttributeConverter;

/**
 * Class to hold failed login attempts. An entry is either for an IP address or
 * for a User, but not both. This way, IP lockouts and User lockouts are
 * independent, and clearing one will not affect the other. Once the number of
 * Attempts for a user or IP reaches a threshold, all Attempts are removed and a
 * LoginLockout is created. Attempts do not expire, but are cleared on successful
 * authentication. If an attempt is for a known username, two objects are
 * created, one for the IP and one for the user. If the username is unknown,
 * then only one is created for the IP.
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "LoginAttempts" )
public class LoginAttempt extends DomainObject<LoginAttempt> {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long     id;

    private String   ip;

    @ManyToOne
    @JoinColumn ( name = "user_id", columnDefinition = "varchar(100)" )
    private User     user;

    @Basic
    // Allows the field to show up nicely in the database
    @Convert( converter = ZonedDateTimeAttributeConverter.class )
    @JsonAdapter( ZonedDateTimeAdapter.class )
    private ZonedDateTime time;

    /**
     * Returns the ID of the Attempt for Hibernate
     *
     * @return the id
     */
    @Override
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

    /**
     * Retrieve a list of all LoginAttempts matching the criterion provided
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LoginAttempt> getWhere ( final List<Criterion> where ) {
        return (List<LoginAttempt>) getWhere( LoginAttempt.class, where );
    }

    /**
     * Returns the number of failed attempts for the given IP.
     *
     * @param addr
     *            The IP to check
     * @return the number of failures from the given IP
     */
    public static int getIPFailures ( final String addr ) {
        return getWhere( eqList( "ip", addr ) ).size();
    }

    /**
     * Clears all entries for the given IP.
     *
     * @param addr
     *            The IP to clear.
     */
    public static void clearIP ( final String addr ) {
        getWhere( eqList( "ip", addr ) ).stream().forEach( e -> e.delete() );
    }

    /**
     * Returns the number of failed attempts for the specified user.
     *
     * @param user
     *            The user to check
     * @return The number of failed attempts for the User.
     */
    public static int getUserFailures ( final User user ) {
        return getWhere( eqList( "user", user ) ).size();
    }

    /**
     * Clears the attempts for the user.
     *
     * @param user
     *            The user to clear.
     */
    public static void clearUser ( final User user ) {
        getWhere( eqList( "user", user ) ).stream().forEach( e -> e.delete() );
    }

}
