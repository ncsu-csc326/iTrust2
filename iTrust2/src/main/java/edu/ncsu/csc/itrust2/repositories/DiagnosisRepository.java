package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    public List<Diagnosis> findByVisit ( OfficeVisit visit );

}
