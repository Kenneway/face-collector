package com.rosetta.face.service.cache;

import com.rosetta.face.domain.Person;
import com.rosetta.face.service.db.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableAsync
@EnableScheduling
@Service
public class PersonCacheService {

    private PersonService personService;

    private volatile Map<Integer, Person> cachedPersonMap = new ConcurrentHashMap<>();

    @Autowired
    public PersonCacheService(PersonService personService) {
        this.personService = personService;
        updatePersonMap();
    }

    @Async
    @Scheduled(fixedRate = 100000)
    public void updatePersonMap() {
        // get all persons
        List<Person> personList = personService.getAllPersons();
        Map<Integer, Person> personMap = new ConcurrentHashMap<>();
        for (Person person : personList) {
            personMap.put(person.getPersonId(), person);
        }
        cachedPersonMap = personMap;
    }

    public Map<Integer, Person> getPersons() {
        return cachedPersonMap;
    }

    public Person getPersonsByPersonId(Integer personId) {
        return cachedPersonMap.get(personId);
    }

}
