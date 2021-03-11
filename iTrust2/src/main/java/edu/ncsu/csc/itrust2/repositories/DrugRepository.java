package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.Drug;

public interface DrugRepository extends JpaRepository<Drug, Long> {

    public boolean existsByCode ( String code );

    public Drug findByCode ( String code );

}
