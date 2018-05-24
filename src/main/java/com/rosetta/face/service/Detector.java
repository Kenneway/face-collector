package com.rosetta.face.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosetta.face.config.bean.DetectorConfigBean;
import com.rosetta.face.domain.Face;

import com.rosetta.face.utils.ImgUtils;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

@Service
public class Detector {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private DetectorConfigBean detectorConfigBean;

    private RestTemplate restTemplate = new RestTemplate();

    public List<Face> detectFace(Mat frame) throws IOException, InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("image", ImgUtils.matToByteArrBase64(frame));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        String res = restTemplate.postForObject(detectorConfigBean.getFeatureUrl(), request , String.class);
        return extractFaces(res);
    }

    public List<Face> recogFace(Mat frame) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("image", ImgUtils.matToByteArrBase64(frame));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        String res = restTemplate.postForObject(detectorConfigBean.getRecognizeUrl(), request , String.class );
        return extractFaces(res);
    }

    private List<Face> extractFaces(String res) {
        List<Face> faces = new ArrayList<>();
        if (res == null || res.length() == 0) return faces;
        JSONObject resObj = JSON.parseObject(res);
        if (resObj.getInteger("code") != 200) return faces;
        String recogDataStr = resObj.getString("data");
        JSONObject recogDataObj = JSON.parseObject(recogDataStr);
        String faceListStr = recogDataObj.getString("face");
        faces = JSON.parseArray(faceListStr, Face.class);
//        System.out.println(JSON.toJSONString(faces));
        return faces;
    }

    public List<Face> test(Mat frame) {
//        FrontResult result = restTemplate.postForObject(detectFaceUrl, mat2String(frame), FrontResult.class);
//        RecogData data = (RecogData) result.getData();
//        return data.getFace();
        return new ArrayList<>();
    }

//    public List<Person> getLastPersons(RecogData lastFaces) {
//        List<Person> persons = new ArrayList<>();
//        for (Face face : lastFaces.getFaces()) {
//            List<Person> findedPerson = personRepository.findByPersonId(face.getPersonId());
//            if (findedPerson.size() > 0) {
//                persons.add(findedPerson.get(0));
//            }
//        }
//        return persons;
//    }

}
