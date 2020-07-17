package com.usta.usta.repositories;

import com.usta.usta.entities.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person,Long> {

    Person findByUsername(String username);
    Person findByActivationCode(String activationCode);
    Person findByEmail(String email);
    Page<Person> findByCategory(String category, Pageable pageable);
}
