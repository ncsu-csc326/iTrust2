package edu.ncsu.csc.itrust2.models.persistent;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
@Table ( name = "LoginLockouts" )
public class LoginLockout extends DomainObject<LoginLockout> {

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
     * Retunrns the ID of the LoginLockout for hibernate.
     *
     * @return the id
     */
    @Override
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

    /**
     * Returns the number of lockouts for the given IP within the timeframe set
     * to ban.
     *
     * @param addr
     *            The IP address associated with lockouts
     * @return The number of lockouts for the given IP
     */
    public static int getRecentIPLockouts ( final String addr ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return getWhere( eqList( "ip", addr ) ).stream()
                .filter( e -> ( now - e.getTime().toEpochSecond() ) < 1440 * 60 * 1000 )
                .collect( Collectors.toList() ).size(); // 1440 minutes
    }

    /**
     * Retrieve a list of Lockouts matching the given criteria
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The list of matching records
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LoginLockout> getWhere ( final List<Criterion> where ) {
        return (List<LoginLockout>) getWhere( LoginLockout.class, where );
    }

    /**
     * Retrieve all LoginLockout entries from the database
     * 
     * @return The List of entries found
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LoginLockout> getLockouts () {
        return (List<LoginLockout>) getAll( LoginLockout.class );
    }

    /**
     * Removes all Lockouts for the given IP.
     *
     * @param addr
     *            The IP to clear
     */
    public static void clearIP ( final String addr ) {
        getWhere( eqList( "ip", addr ) ).stream().forEach( e -> e.delete() );
    }

    /**
     * Returns true if the given IP is locked out currently.
     *
     * @param addr
     *            The IP to check.
     * @return true if IP is locked out, flase otherwise
     */
    public static boolean isIPLocked ( final String addr ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return getWhere( eqList( "ip", addr ) ).stream()
                .filter( e -> ( now - e.getTime().toEpochSecond() ) < 60 * 60 * 1000 ).collect( Collectors.toList() )
                .size() > 0; // locked if within 60 minutes

    }

    /**
     * Returns the number of lockouts within the timeframe for a ban for the
     * given User.
     *
     * @param user
     *            The User to check
     * @return The number of lockouts for the user
     */
    public static int getRecentUserLockouts ( final User user ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return getWhere( eqList( "user", user ) ).stream()
                .filter( e -> ( now - e.getTime().toEpochSecond() ) < 1440 * 60 * 1000 )
                .collect( Collectors.toList() ).size(); // 1440 minutes
    }

    /**
     * Clears the lockouts for the given user.
     *
     * @param user
     *            The User to clear.
     */
    public static void clearUser ( final User user ) {
        getWhere( eqList( "user", user ) ).stream().forEach( e -> e.delete() );
    }

    /**
     * Checks if the given User is currently locked out.
     *
     * @param user
     *            The User to check
     * @return true if the user is locked out, false otherwise
     */
    public static boolean isUserLocked ( final User user ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return getWhere( eqList( "user", user ) ).stream()
                .filter( e -> ( now - e.getTime().toEpochSecond() ) < 60 * 60 * 1000 ).collect( Collectors.toList() )
                .size() > 0; // locked if within 60 minutes
    }

}
