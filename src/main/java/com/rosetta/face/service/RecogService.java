package com.rosetta.face.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosetta.face.domain.Face;

import com.rosetta.face.domain.Person;
import com.rosetta.face.service.cache.FreshFaceCacheService;
import com.rosetta.face.service.cache.PersonCacheService;
import com.rosetta.face.service.cache.RecordFaceCacheService;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableAsync
@EnableScheduling
@Service
public class RecogService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Detector detector;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private PersonCacheService personCacheService;

    @Autowired
    private FreshFaceCacheService freshFaceCacheService;

    @Autowired
    private RecordFaceCacheService recordCacheService;

    private volatile Map<String, List<Face> > lastFaces = new ConcurrentHashMap<>();

    private Float cosineAngleThres = 50f;

//    @Scheduled(cron = "*/2 * * * * ?")
    @Async
    @Scheduled(fixedRate = 100)
    public void recogService() throws IOException {

        long t0 = System.currentTimeMillis();
        System.out.println("RecogService.recogService: Scheduled.current=" + t0);
        Map<String, Camera> cameras = cameraService.getCameras();
        for (Map.Entry<String, Camera> entry : cameras.entrySet()) {
            String cameraId = entry.getKey();
            Camera camera = entry.getValue();
            Mat lastFrame = new Mat();
            synchronized (camera) {
                camera.getLastFrame().copyTo(lastFrame);
            }
            // 只有person_id
            long t1 = System.currentTimeMillis();
            List<Face> lastFace = detector.recogFace(lastFrame);
            long t2 = System.currentTimeMillis();
            lastFrame.release();
            List<Face> validFace = verifyFace(lastFace, cameraId);
            long t3 = System.currentTimeMillis();
            putCachedFaces(validFace);
            String facesName = "";
            for (Face face : validFace) {
                facesName += "[" + face.getName() + "]";
            }
            String recogLog = String.format("RecogService.recogService.camera[%s]: recogTime=%dms; recogSize=%d;  validTime=%d; validName=%s;",
                                            cameraId,
                                            t2-t1,
                                            lastFace.size(),
                                            t3-t2,
                                            facesName);
            System.out.println(recogLog);
            lastFaces.put(cameraId, validFace);
        }
        System.out.println("RecogService.recogService: time=" + (System.currentTimeMillis() - t0) + "ms");

    }

    private void putCachedFaces(List<Face> faces) {
        for (Face face : faces) {
            if (face.getPersonId() < 0) continue;
            if (recordCacheService.containsKey(face.getPersonId())) continue;
            face.setUpdateTime(System.currentTimeMillis());
            recordCacheService.put(face.getPersonId(), face);
        }
    }

    private List<Face> verifyFace (List<Face> faces,
                                   String cameraId) throws IOException {

        List<Face> validFace = new ArrayList<>();
        for (Face face : faces) {
            face.setCameraId(cameraId);
            if (face.getPersonId() == -1) {
                List<Float> feature = face.getFeature();
                List<Face> freshFaces = freshFaceCacheService.getFreshFaces();
                Face checkedFace = checkFreshFaces(feature, freshFaces);
                if (checkedFace != null) {
                    Face newFace = fillFace(face, checkedFace.getPersonId());
                    if (newFace != null) validFace.add(newFace);
                }
            } else if (face.getPersonId() > 0) {
                Face newFace = fillFace(face, face.getPersonId());
                if (newFace != null) validFace.add(newFace);
            }
        }
        return validFace;
    }

    private Face fillFace(Face face, Integer personId) {
        Person person = personCacheService.getPersonsByPersonId(personId);
        if (person == null) return null;
        face.setPersonId(personId);
        face.setName(person.getName());
        face.setFaviconUrl(person.getFaviconUrl());
        return face;
    }

    private Face checkFreshFaces(List<Float> feature,
                                 List<Face> freshFaces) throws IOException {
        Face targetFace = null;
        Float minAngle = cosineAngleThres;

        for (Face freshFace : freshFaces) {
            String freshFaceFeatureJson = freshFace.getFeatureJson();
            List<Float> freshFaceFeature = mapper.readValue(freshFaceFeatureJson, new TypeReference<List<Float>>(){});
            if (freshFaceFeature.size() != 1024) continue;
            Float angle = cosineAngle(feature, freshFaceFeature);
            if (angle == null) continue;
            if (angle < minAngle) {
                minAngle = angle;
                targetFace = freshFace;
            }
        }
        return targetFace;
    }

    public Float cosineAngle(List<Float> feature1, List<Float> feature2) {
        if (feature1.size() != feature2.size()
                || feature1.size() == 0) return null;
        Float product = 0f;
        Float norm1 = 0f;
        Float norm2 = 0f;
        for (int i=0; i<feature1.size(); i++) {
            product += feature1.get(i) * feature2.get(i);
            norm1 += feature1.get(i) * feature1.get(i);
            norm2 += feature2.get(i) * feature2.get(i);
        }
        double norm = Math.sqrt(norm1 * norm2);
        if (norm == 0) return null;
        double cosine = product / norm;
        double res = Math.acos(cosine);
        return (float)(res * 180 / Math.PI);
    }

    public List<Face> recogLast(String id) {
        List<Face> faces = lastFaces.get(id);
        if (faces == null) return new ArrayList<Face>();
        System.out.println("RecogService.recogLast: " + faces.size());
        return faces;
    }

    /**************************************************** Test ********************************************************/

    //    @Scheduled(fixedRate = 5000)
    public void test() throws IOException {

        System.out.println("============== test recog ==============");
        System.out.println("Camera01:" + mapper.writeValueAsString(lastFaces.get("01")));
        System.out.println("Camera02:" + mapper.writeValueAsString(lastFaces.get("02")));
    }

}
