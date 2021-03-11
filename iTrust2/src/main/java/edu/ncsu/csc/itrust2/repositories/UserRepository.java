package edu.ncsu.csc.iTrust2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.iTrust2.models.User;

public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername ( String username );

}
