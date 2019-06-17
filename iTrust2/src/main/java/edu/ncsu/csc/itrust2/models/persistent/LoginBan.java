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
 * Contains info about a LoginBan from the system. A ban does not expire, and
 * can only be removed by an admin (Not Implemented). A ban can be for either a
 * User or an IP.
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "LoginBans" )
public class LoginBan extends DomainObject<LoginBan> {

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
     * Returns the ID of the LoginBan for Hibernate
     *
     * @return the id
     */
    @Override
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

    /**
     * Retrieves all LoginBans for the criteria provided.
     * 
     * @param A
     *            List of Criterion to search by to constrain what is retrieved
     * @return The list of all matching LoginBans
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LoginBan> getWhere ( final List<Criterion> where ) {
        return (List<LoginBan>) getWhere( LoginBan.class, where );
    }

    /**
     * Returns true if the given IP is banned.
     *
     * @param addr
     *            The IP to check
     * @return true if banned, false otherwise
     */
    public static boolean isIPBanned ( final String addr ) {
        return getWhere( eqList( "ip", addr ) ).size() > 0;
    }

    /**
     * Returns true if the given user is banned.
     *
     * @param user
     *            The user to check
     * @return true if banned, false otherwise.
     */
    public static boolean isUserBanned ( final User user ) {
        return getWhere( eqList( "user", user ) ).size() > 0;
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
     * Clears the bans for the user.
     *
     * @param user
     *            The user to clear.
     */
    public static void clearUser ( final User user ) {
        getWhere( eqList( "user", user ) ).stream().forEach( e -> e.delete() );
    }
}
