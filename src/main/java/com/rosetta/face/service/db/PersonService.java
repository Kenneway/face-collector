package com.rosetta.face.service.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosetta.face.domain.Person;
import com.rosetta.face.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Map;

@Service
public class PersonService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PersonRepository personRepo;

    public List<Person> getAllPersons() {
        return personRepo.findAll();
    }

    public Person addPerson(Person person) {
        return personRepo.save(person);
    }

    public void delPerson(int id) {
        personRepo.deleteById(id);
    }

    public void setPerson(Person person) {
        personRepo.save(person);
    }

    public Person getPerson(int id) {
        return personRepo.getOne(id);
    }

    public List<Person> getPersonByName(String name) {
        return personRepo.findByName(name);
    }

    public List<Person> getPersonByPersonId(Integer personId) {
        return personRepo.findByPersonId(personId);
    }

    public List<Person> getAllFreshmen() {
        return  personRepo.findByStatus(1);
    }

//    @Scheduled(fixedRate = 2000)
//    private void test() throws JsonProcessingException {
//        List<Person> persons = getPersonByPersonId(10598);
//        for (Person p : persons) {
//            System.out.println("==============yyyyyyyyyyyyyy======================" + p.getFaviconUrl());
//        }
//    }

}
