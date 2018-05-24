package com.rosetta.face.service.cache;

import com.rosetta.face.domain.Face;
import com.rosetta.face.domain.Person;
import com.rosetta.face.service.db.FaceService;
import com.rosetta.face.service.db.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableAsync
@EnableScheduling
@Service
public class FreshFaceCacheService {

    private FaceService faceService;

    private PersonCacheService personCacheService;

    private volatile List<Face> freshFaces;

    @Autowired
    public FreshFaceCacheService(FaceService faceService, PersonCacheService personCacheService) {
        this.faceService = faceService;
        this.personCacheService = personCacheService;
        updateFreshFaces();
    }

    @Async
    @Scheduled(fixedRate = 100000)
    public void updateFreshFaces() {

        // get all faces
        List<Face> rawFreshFaces = faceService.getAllFreshFaces();
        List<Face> newFreshFaces = new ArrayList<Face>();
        for (Face face : rawFreshFaces) {
            Integer personId = face.getPersonId();
            Person person = personCacheService.getPersonsByPersonId(personId);
            if (person == null) continue;
            face.setName(person.getName());
            face.setFaviconUrl(person.getFaviconUrl());
            newFreshFaces.add(face);
        }
        freshFaces = newFreshFaces;
        System.out.println("FreshmanService.Freshmen: updated");
    }

    public List<Face> getFreshFaces() {
        return freshFaces;
    }

}
