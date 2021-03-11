package edu.ncsu.csc.iTrust2.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.DiagnosisForm;
import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.DiagnosisRepository;

@Component
@Transactional
public class DiagnosisService extends Service {

    @Autowired
    private DiagnosisRepository repository;

    @Autowired
    private OfficeVisitService  service;

    @Autowired
    private ICDCodeService      icdCodeService;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

    public Diagnosis build ( final DiagnosisForm form ) {
        final Diagnosis diag = new Diagnosis();
        diag.setVisit( (OfficeVisit) service.findById( form.getVisit() ) );
        diag.setNote( form.getNote() );
        diag.setCode( icdCodeService.findByCode( form.getCode() ) );
        diag.setId( form.getId() );

        return diag;
    }

    public List<Diagnosis> findByPatient ( final User patient ) {
        return service.findByPatient( patient ).stream().map( e -> findByVisit( e ) ).flatMap( e -> e.stream() )
                .collect( Collectors.toList() );

    }

    public List<Diagnosis> findByVisit ( final OfficeVisit visit ) {
        return repository.findByVisit( visit );
    }

}
