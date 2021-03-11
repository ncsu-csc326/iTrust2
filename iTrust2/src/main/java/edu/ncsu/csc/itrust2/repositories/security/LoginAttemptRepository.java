package edu.ncsu.csc.iTrust2.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.security.LoginAttempt;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    /**
     * Counts the number of LoginAttempts for the given IP address.
     * 
     * @param ipAddress
     *            IP address to search on
     * @return The number of matched LoginAttempts.
     */
    public long countByIp ( String ipAddress );

    /**
     * Deletes all saved LoginAttempt records for the given IP address.
     * 
     * @param ipAddress
     *            The IP address to delete by
     * @return The number of records deleted
     */
    public long deleteByIp ( String ipAddress );

    /**
     * Counts the number of LoginAttempts for the given user.
     * 
     * @param user
     *            The User to search on
     * @return The number of matched LoginAttempts.
     */
    public long countByUser ( User user );

    /**
     * Deletes all saved LoginAttempt records for the given user.
     * 
     * @param user
     *            The User to delete by.
     * @return The number of records deleted
     */
    public long deleteByUser ( User user );

}
