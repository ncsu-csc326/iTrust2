package edu.ncsu.csc.itrust2.models.persistent;

import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Persistence class that holds a Password Reset Token that is used to verify
 * users who forgot their password. It contains the user who requested it, a
 * timestamp, and a temporary password the user needs to reset their password.
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "PasswordResetTokens" )
public class PasswordResetToken extends DomainObject<PasswordResetToken> {

    /**
     * Only for testing without email access. No functionality should use this
     */
    public static PasswordResetToken lastToken      = null;
    /** Time that tokens are valid */
    public static final long         TOKEN_LIFETIME = 10 * 60000; // 10 minutes

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "user_id", columnDefinition = "varchar(100)" )
    private User                     user;
    @NotNull
    private long                     creationTime;
    @NotNull
    private String                   tempPassword;
    /**
     * Stored here so email knows what to say. Transient to prevent persistent
     * storage
     */
    private transient String         tempPasswordPlaintext;
    @Id
    @NotNull
    private long                     id;

    /**
     * Empty constructor for hibernate
     */
    public PasswordResetToken () {

    }

    /** The list of characters used to generate temporary tokens */
    private static String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()";

    /**
     * Constructs a Password Reset Token for the given user. This is where the
     * random temporary password is generated.
     *
     * @param user
     *            The user the token belongs to
     */
    public PasswordResetToken ( final User user ) {
        setUser( user );
        creationTime = System.currentTimeMillis();
        final Random rand = new Random();
        String token = "";
        for ( int i = 0; i < 10; i++ ) {
            token += chars.charAt( rand.nextInt( chars.length() ) );
        }
        tempPasswordPlaintext = token;
        final PasswordEncoder pe = new BCryptPasswordEncoder();
        setTempPassword( pe.encode( tempPasswordPlaintext ) );
        long id2 = rand.nextLong();
        while ( id2 <= 0 || getById( id2 ) != null ) {
            id2 = rand.nextLong();
        }
        setId( id2 );
        lastToken = this;
    }

    /**
     * Returns the user of this Token
     *
     * @return the user
     */
    public User getUser () {
        return user;
    }

    /**
     * Sets the user for this token
     *
     * @param user
     *            the user to set
     */
    public void setUser ( final User user ) {
        this.user = user;
    }

    /**
     * Returns the time this token was created in epoch MS
     *
     * @return the creationTime
     */
    public long getCreationTime () {
        return creationTime;
    }

    /**
     * Sets the creation time of this token.
     *
     * @param creationTime
     *            the creationTime to set (in epoch ms)
     */
    public void setCreationTime ( final long creationTime ) {
        this.creationTime = creationTime;
    }

    /**
     * Returns the hashed & salted temporary password associated with this reset
     *
     * @return the tempPassword
     */
    public String getTempPassword () {
        return tempPassword;
    }

    /**
     * Sets the hashed and salted temporary password
     *
     * @param tempPassword
     *            the tempPassword to set
     */
    public void setTempPassword ( final String tempPassword ) {
        this.tempPassword = tempPassword;
    }

    /**
     * Returns the ID of this token
     *
     * @return the id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of this token
     *
     * @param id
     *            the id to set
     */
    public void setId ( final long id ) {
        this.id = id;
    }

    /**
     * Returns the plaintext password. This will return null if this object was
     * loaded from the database.
     *
     * @return The plaintext password or null if loaded from db.
     */
    public String getTempPasswordPlaintext () {
        return tempPasswordPlaintext;
    }

    /**
     * Sets the plaintext temporary password. This method call has no effect on
     * the hashed password
     *
     * @param pw
     *            The new plaintext password
     */
    public void setTempPasswordPlaintext ( final String pw ) {
        this.tempPasswordPlaintext = pw;
    }

    /**
     * Checks if the token has expired, which occurs if more than TOKEN_LIFETIME
     * has passed since its creation.
     *
     * @return true if this token is expired, false if still valid
     */
    public boolean isExpired () {
        return System.currentTimeMillis() - creationTime > TOKEN_LIFETIME;
    }

    /**
     * Get a specific token by the database ID
     *
     * @param id
     *            the database ID
     * @return the reset token with the desired ID
     */
    public static PasswordResetToken getById ( final Long id ) {
        try {
            return (PasswordResetToken) getWhere( PasswordResetToken.class, createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

}
