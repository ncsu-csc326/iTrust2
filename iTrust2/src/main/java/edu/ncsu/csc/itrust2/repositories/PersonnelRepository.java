package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Personnel;

public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

}
