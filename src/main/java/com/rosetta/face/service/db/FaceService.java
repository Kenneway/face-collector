package com.rosetta.face.service.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosetta.face.domain.Face;
import com.rosetta.face.repository.FaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaceService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FaceRepository faceRepo;

    public List<Face> getAll() {
        return faceRepo.findAll();
    }

    public Face addFace(Face face) {
        return faceRepo.save(face);
    }

    public void delFace(long id) {
        faceRepo.deleteById(id);
    }

    public void setFace(Face face) {
        faceRepo.save(face);
    }

    public Face getFace(long id) {
        return faceRepo.getOne(id);
    }

    public void addFace(Integer personId,
                        String featureJson) throws JsonProcessingException {

    }

    public List<Face> getFaceByPersonId(Integer personId) {
        return faceRepo.findByPersonId(personId);
    }

    public List<Face> getFaceByPersonIdAndCameraId(Integer personId, String cameraId) {
        return faceRepo.findByPersonIdAndCameraId(personId, cameraId);
    }

    public List<Face> getAllFreshFaces() {
        return  faceRepo.findByStatus(1);
    }

//    @Scheduled(fixedRate = 2000)
//    private void test() throws JsonProcessingException {
//        Person p = new Person();
//        p.setStatus(0);
//        p.setFeatures("[677,89,8]");
//        p.setPersonId(23);
//        p.setName("哈哈");
//        setPerson(p);
//        System.out.println(mapper.writeValueAsString(p));
//    }

}
