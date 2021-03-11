package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.repositories.PatientRepository;

@Component
@Transactional
public class PatientService extends UserService {

    @Autowired
    private PatientRepository repository;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

}
