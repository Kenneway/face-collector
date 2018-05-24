package com.rosetta.face.repository;

import com.rosetta.face.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    public List<Person> findByName(String name);

    public List<Person> findByDuty(String duty);

    public List<Person> findByPersonId(Integer personId);

    public List<Person> findByStatus(Integer status);

}
