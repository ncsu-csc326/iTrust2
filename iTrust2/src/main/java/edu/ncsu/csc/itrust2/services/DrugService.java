package edu.ncsu.csc.iTrust2.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Drug;
import edu.ncsu.csc.iTrust2.repositories.DrugRepository;

@Component
@Transactional
public class DrugService extends Service {

    @Autowired
    private DrugRepository repository;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

    public boolean existsByCode ( final String code ) {
        return repository.existsByCode( code );
    }

    public Drug findByCode ( final String code ) {
        return repository.findByCode( code );
    }
}
