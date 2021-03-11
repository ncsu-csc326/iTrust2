package edu.ncsu.csc.iTrust2.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Email;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.EmailRepository;

@Component
@Transactional
public class EmailService extends Service {

    @Autowired
    private EmailRepository repository;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

    public List<Email> findByReceiver ( final User receiver ) {
        return repository.findByReceiver( receiver );
    }
}
