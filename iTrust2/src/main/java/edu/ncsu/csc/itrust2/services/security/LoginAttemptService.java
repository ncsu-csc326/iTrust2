package edu.ncsu.csc.iTrust2.services.security;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.security.LoginAttemptRepository;
import edu.ncsu.csc.iTrust2.services.Service;

@Component
@Transactional
public class LoginAttemptService extends Service {

    @Autowired
    private LoginAttemptRepository repository;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

    public long countByIP ( final String ipAddress ) {
        return repository.countByIp( ipAddress );
    }

    public long clearIP ( final String ipAddress ) {
        return repository.deleteByIp( ipAddress );
    }

    public long countByUser ( final User user ) {
        return repository.countByUser( user );
    }

    public long clearUser ( final User user ) {
        return repository.deleteByUser( user );
    }

}
