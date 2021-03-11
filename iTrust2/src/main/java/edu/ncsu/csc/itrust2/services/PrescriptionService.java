package edu.ncsu.csc.iTrust2.services;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.forms.PrescriptionForm;
import edu.ncsu.csc.iTrust2.models.Prescription;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.repositories.PrescriptionRepository;

@Component
@Transactional
public class PrescriptionService extends Service {

    @Autowired
    private PrescriptionRepository repository;

    @Autowired
    private DrugService            drugService;

    @Autowired
    private UserService            userService;

    @Override
    protected JpaRepository getRepository () {
        return repository;
    }

    public Prescription build ( final PrescriptionForm form ) {
        final Prescription pr = new Prescription();

        pr.setDrug( drugService.findByCode( form.getDrug() ) );
        pr.setDosage( form.getDosage() );
        pr.setRenewals( form.getRenewals() );
        pr.setPatient( userService.findByName( form.getPatient() ) );

        if ( form.getId() != null ) {
            pr.setId( form.getId() );
        }

        pr.setStartDate( LocalDate.parse( form.getStartDate() ) );
        pr.setEndDate( LocalDate.parse( form.getEndDate() ) );

        return pr;
    }

    public List<Prescription> findByPatient ( final User patient ) {
        return repository.findByPatient( patient );
    }

}
