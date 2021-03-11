package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Prescription;
import edu.ncsu.csc.iTrust2.models.User;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    public List<Prescription> findByPatient ( final User patient );

}
