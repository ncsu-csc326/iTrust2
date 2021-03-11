package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.ICDCode;

public interface ICDCodeRepository extends JpaRepository<ICDCode, Long> {

    public ICDCode findByCode ( String code );

}
