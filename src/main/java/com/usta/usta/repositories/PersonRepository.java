package com.usta.usta.repositories;

import com.usta.usta.entities.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;

public interface PersonRepository extends JpaRepository<Person,Long> {

    Page<Person> findAll(Pageable pageable);
    Person findByUsername(String username);
    Person findByActivationCode(String activationCode);
    Person findByEmail(String email);

    @Query("select person from Person person where person.category like :paramCategory and person.isActivated=true ")
    Page<Person> findByCategory(@Param("paramCategory") String category, Pageable pageable);
}
