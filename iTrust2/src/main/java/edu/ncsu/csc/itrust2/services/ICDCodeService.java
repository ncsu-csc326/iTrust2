package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.ICDCode;
import edu.ncsu.csc.iTrust2.repositories.ICDCodeRepository;

@Component
@Transactional
public class ICDCodeService extends Service {

    @Autowired
    private ICDCodeRepository repository;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

    public ICDCode findByCode ( final String code ) {
        return repository.findByCode( code );
    }

}
