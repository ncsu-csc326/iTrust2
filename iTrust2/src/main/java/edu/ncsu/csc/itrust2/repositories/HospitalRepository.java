package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, String> {

    public Hospital findByName ( String name );

}
