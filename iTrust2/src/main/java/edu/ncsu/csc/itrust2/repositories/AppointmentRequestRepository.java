package edu.ncsu.csc.iTrust2.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.AppointmentRequest;
import edu.ncsu.csc.iTrust2.models.User;

public interface AppointmentRequestRepository extends JpaRepository<AppointmentRequest, Long> {

    public List<AppointmentRequest> findByPatient ( User patient );

    public List<AppointmentRequest> findByHcp ( User hcp );

    public List<AppointmentRequest> findByHcpAndPatient ( User hcp, User patient );

}
