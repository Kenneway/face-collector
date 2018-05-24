package com.rosetta.face.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosetta.face.domain.Face;
import com.rosetta.face.domain.RecogData;
import com.rosetta.face.dto.FrontData;
import com.rosetta.face.dto.FrontFace;
import com.rosetta.face.dto.FrontResult;
import com.rosetta.face.service.RecogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * face attendancer controller
 *
 */
@RestController
@RequestMapping("/attend/api")
public class AttendController {

    ObjectMapper mapper = new ObjectMapper();

    @Value("${static.faviconRepoContext}")
    private String faviconRepoContext;

    @Value("${static.faviconRepoUrl}")
    private String faviconRepoUrl;

    @Value("${static.faviconDefaultUrl}")
    private String faviconDefaultUrl;

    @Autowired
    private RecogService recogService;

    @PostMapping("/{id}/recog")
    public FrontResult recogFace(@PathVariable("id") String id) {
        long start = System.currentTimeMillis();
        FrontResult result = new FrontResult();
        try {
            List<Face> faceList = recogService.recogLast(id);
            FrontData frontData = new FrontData();
            List<FrontFace> frontFaceList = new ArrayList<>();
            for (Face face : faceList) {
                String faviconUrl = face.getFaviconUrl();
                if (faviconUrl == null || faviconUrl.length() == 0){
                    faviconUrl = faviconDefaultUrl;
                } else {
                    faviconUrl = faviconUrl.replace(faviconRepoContext, faviconRepoUrl);
                }
                face.setFaviconUrl(faviconUrl);
                frontFaceList.add(new FrontFace(face));
            }
            frontData.setFace(frontFaceList);
            result.setCode(200);
            result.setMsg("SUCCESS.");
            result.setData(frontData);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500);
            result.setMsg("FAIL.");
            return result;
        }
        System.out.println("AttendController.recogFace: time=" + (System.currentTimeMillis()-start) + "ms");
        return result;
    }

//    @Scheduled(fixedRate = 10000)
    private void test() throws JsonProcessingException {
        List<Face> faces = recogService.recogLast("01");
        System.out.println("RecogService: Cache Layer has been flushed.");
    }

    @PostMapping("/update/freshman")
    public FrontResult updateFreshman() {
        return new FrontResult();
    }

}
