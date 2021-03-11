package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.repositories.HospitalRepository;

@Component
@Transactional
public class HospitalService extends Service {

    @Autowired
    private HospitalRepository repository;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

    public Hospital findByName ( final String name ) {
        return repository.findByName( name );
    }

}
