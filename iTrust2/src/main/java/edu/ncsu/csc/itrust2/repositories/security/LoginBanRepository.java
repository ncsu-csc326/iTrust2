package edu.ncsu.csc.iTrust2.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.security.LoginBan;

public interface LoginBanRepository extends JpaRepository<LoginBan, Long> {

    /**
     * Checks to see if there is a LoginBan for the given IP address.
     *
     * @param ipAddress
     *            The IP address to search by.
     * @return True iff a ban was found.
     */
    public boolean existsByIp ( String ipAddress );

    /**
     * Checks to see if there is a LoginBan for the given user.
     *
     * @param user
     *            The User to search by.
     * @return True iff a ban was found.
     */
    public boolean existsByUser ( User user );

    /**
     * Deletes all saved LoginBans for the given IP address.
     *
     * @param ipAddress
     *            The IP address to delete by
     * @return The number of records deleted.
     */
    public long deleteByIp ( String ipAddress );

    /**
     * Deletes all saved LoginBans for the given user.
     * 
     * @param user
     *            The User to delete by.
     * @return The number of records deleted.
     */
    public long deleteByUser ( User user );

}
