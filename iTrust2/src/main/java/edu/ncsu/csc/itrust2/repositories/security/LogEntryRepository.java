package edu.ncsu.csc.iTrust2.repositories.security;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.ncsu.csc.iTrust2.models.security.LogEntry;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    /**
     * Retrieves a list of LogEntry records between (inclusive) two dates.
     *
     * @param fromDate
     *            Starting date
     * @param toDate
     *            Ending date
     * @return List of matching records
     */
    public List<LogEntry> findByTimeBetween ( ZonedDateTime fromDate, ZonedDateTime toDate );

    /**
     * Retrieves all log entries for a user where they are either the primary or
     * secondary user on the Entry.
     *
     * @param user
     *            User to search on
     * @return List of matching records
     */
    @Query ( "SELECT le FROM LogEntry le WHERE le.primaryUser = ?1 OR le.secondaryUser = ?1" )
    public List<LogEntry> findByPrimaryUserOrSecondaryUser ( String user );

}
