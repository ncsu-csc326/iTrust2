package edu.ncsu.csc.iTrust2.services.security;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.security.LoginLockoutRepository;
import edu.ncsu.csc.iTrust2.services.Service;

@Component
@Transactional
public class LoginLockoutService extends Service {

    @Autowired
    private LoginLockoutRepository repository;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

    public boolean isIPLocked ( final String ipAddress ) {
        final long now = ZonedDateTime.now().toEpochSecond();

        return repository.findByIp( ipAddress ).stream().filter( e -> ( now - e.getTime().toEpochSecond() ) < 60 * 60 )
                .collect( Collectors.toList() ).size() > 0; // locked if within
                                                            // 60 minutes
    }

    public long clearIP ( final String ipAddress ) {
        return repository.deleteByIp( ipAddress );

    }

    public int getRecentIPLockouts ( final String ipAddress ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return repository.findByIp( ipAddress ).stream()
                .filter( e -> ( now - e.getTime().toEpochSecond() ) < 1440 * 60 ).collect( Collectors.toList() ).size(); // 1440
                                                                                                                         // minutes
    }

    public int getRecentUserLockouts ( final User user ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return repository.findByUser( user ).stream().filter( e -> ( now - e.getTime().toEpochSecond() ) < 1440 * 60 )
                .collect( Collectors.toList() ).size(); // 1440 minutes
    }

    public long clearUser ( final User user ) {
        return repository.deleteByUser( user );
    }

    public boolean isUserLocked ( final User user ) {
        final long now = ZonedDateTime.now().toEpochSecond();
        return repository.findByUser( user ).stream().filter( e -> ( now - e.getTime().toEpochSecond() ) < 60 * 60 )
                .collect( Collectors.toList() ).size() > 0; // locked if within
                                                            // 60 minutes
    }

}
