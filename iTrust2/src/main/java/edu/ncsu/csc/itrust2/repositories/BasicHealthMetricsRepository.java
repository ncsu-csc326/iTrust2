package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.BasicHealthMetrics;

public interface BasicHealthMetricsRepository extends JpaRepository<BasicHealthMetrics, Long> {

}
