package edu.ncsu.csc.iTrust2.services.security;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.security.LogEntry;
import edu.ncsu.csc.iTrust2.repositories.security.LogEntryRepository;
import edu.ncsu.csc.iTrust2.services.Service;

@Component
@Transactional
public class LogEntryService extends Service {

    @Autowired
    private LogEntryRepository repository;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

    public List<LogEntry> findAllForUser ( final String user ) {
        return repository.findByPrimaryUserOrSecondaryUser( user );
    }

    public List<LogEntry> findByDateRange ( final String user, final ZonedDateTime startDate,
            final ZonedDateTime endDate ) {

        final List<LogEntry> withinRange = repository.findByTimeBetween( startDate, endDate );

        return withinRange.stream()
                .filter( e -> e.getPrimaryUser().equals( user ) || e.getSecondaryUser().equals( user ) )
                .collect( Collectors.toList() );

    }

}
