package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.User;

public interface OfficeVisitRepository extends JpaRepository<OfficeVisit, Long> {

    public List<OfficeVisit> findByHcp ( User hcp );

    public List<OfficeVisit> findByPatient ( User patient );

    public List<OfficeVisit> findByHcpAndPatient ( User hcp, User patient );

}
