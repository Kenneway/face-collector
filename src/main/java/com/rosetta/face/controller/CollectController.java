package com.rosetta.face.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosetta.face.config.bean.FileServerConfigBean;
import com.rosetta.face.domain.Face;
import com.rosetta.face.domain.Person;
import com.rosetta.face.dto.FrontData;
import com.rosetta.face.dto.FrontFace;
import com.rosetta.face.dto.FrontResult;
import com.rosetta.face.service.DetectService;
import com.rosetta.face.service.FTPService;
import com.rosetta.face.service.db.FaceService;
import com.rosetta.face.service.db.PersonService;
import com.rosetta.face.utils.FtpUtils;
import com.rosetta.face.utils.ImgUtils;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * face collector controller
 *
 */
@RestController
@RequestMapping("/collect/api")
public class CollectController {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private DetectService detectService;

    @Autowired
    private PersonService personService;

    @Autowired
    private FaceService faceService;

    @Autowired
    private FileServerConfigBean fileServerConfigBean;

    @Value("${static.faviconRepoContext}")
    private String faviconRepoContext;

    @Value("${static.faviconRepoDir}")
    private String faviconRepoDir;

    @Value("${static.faceRepoDir}")
    private String faceRepoDir;

    @PostMapping("/{id}/detect")
    public FrontResult detectFace(@PathVariable("id") String cameraId) {

        FrontResult result = new FrontResult();
        try {
            List<Face> faceList = detectService.detectLast(cameraId);
            System.out.println("CollectController.detectFace.camera[" + cameraId + "].face： " + faceList.size());
            FrontData frontData = new FrontData();
            List<FrontFace> frontFaceList = new ArrayList<>();
            for (Face face : faceList) {
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
        return result;
    }

    @PostMapping("/{id}/save/person")
//    @ResponseBody
    public FrontResult savePerson(@PathVariable("id") String cameraId,
                                  @RequestParam("person_id") String personIdStr,
                                  @RequestParam("name") String name,
                                  @RequestParam(required=false, value="favicon") MultipartFile faviconFile,
                                  @RequestParam("face") String frontFaceArrJson) {

        FrontResult result = new FrontResult();
        try {
            // check person id
            Integer personId = Integer.parseInt(personIdStr);
            if (personId == null || personId == 0) {
                throw new Exception("person_id不能为空");
            }

            // save favicon
            String faviconUrl = saveFavicon(personId, faviconFile);

            // update person table
            updatePersonTable(personId, name, faviconUrl);

            // update face table
            updateFaceTable(personId, cameraId);

            // delete face dir
            FTPService ftpService = new FTPService(fileServerConfigBean);
            ftpService.rmdirs(fileServerConfigBean.getNginx().get("facePath") + "/" + personId + "/" + cameraId);
            ftpService.close();

            // save face
            List<FrontFace> frontFaceArr = JSON.parseArray(frontFaceArrJson, FrontFace.class);
            for (FrontFace frontFace : frontFaceArr) {
                Face face = new Face();
                List<Float> feature = frontFace.getFeature();
                String featureJson = JSON.toJSONString(feature);
                face.setFeatureJson(featureJson);
                face.setImageBase64(frontFace.getImageBase64());
                saveFace(personId, cameraId, face);
            }
            result.setCode(200);
            result.setMsg("SUCCESS.");
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500);
            result.setMsg("FAIL.");
            return result;
        }
        return result;
    }

    @PostMapping("/file/save/person")
//    @ResponseBody
    public FrontResult savePerson(@RequestParam("person_id") String personIdStr,
                                  @RequestParam("name") String name,
                                  @RequestParam(required=false, value="favicon") MultipartFile faviconFile,
                                  @RequestParam("face") MultipartFile[] faceFiles) {

        FrontResult result = new FrontResult();
        String cameraId = "FILE";
        try {
            // check person id
            Integer personId = Integer.parseInt(personIdStr);
            if (personId == null || personId == 0) {
                throw new Exception("person_id不能为空");
            }

            // save favicon
            String faviconUrl = saveFavicon(personId, faviconFile);

            // update person table
            updatePersonTable(personId, name, faviconUrl);

            // update face table
            updateFaceTable(personId, cameraId);

            // detect face
            List<Face> resFaceList = new ArrayList<>();
            for (MultipartFile faceFile : faceFiles) {
                InputStream faceInputStream = faceFile.getInputStream();
                byte[] faceByteArr = ImgUtils.inputStream2ByteArr(faceInputStream);
                Mat faceMat = ImgUtils.byteArr2Mat(faceByteArr);
                List<Face> faceList = detectService.detectImage(faceMat);
                resFaceList.addAll(faceList);
            }

            // save face
            for (Face face : resFaceList) {
                saveFace(personId, cameraId, face);
            }
            result.setCode(200);
            result.setMsg("SUCCESS.");
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500);
            result.setMsg("FAIL.");
            return result;
        }
        return result;
    }

    // save favicon
    private String saveFavicon(Integer personId,
                               MultipartFile faviconFile) throws IOException {
        String faviconUrl = fileServerConfigBean.getNginx().get("faviconUrl");
        String faviconName = null;
        if (faviconFile != null && faviconFile.getSize() > 0) {
            // favicon name
            // 解决中文问题，liunx下中文路径，图片显示问题
            // fileName = UUID.randomUUID() + suffixName;
            String srcFileName = faviconFile.getOriginalFilename();
            String suffixName = srcFileName.substring(srcFileName.lastIndexOf("."));
            faviconName = String.format("%08d", personId) + suffixName;
            InputStream faviconInputStream = faviconFile.getInputStream();


            FtpUtils.uploadFile(
                    fileServerConfigBean.getIp(),
                    Integer.parseInt(fileServerConfigBean.getFtp().get("port")),
                    fileServerConfigBean.getFtp().get("username"),
                    fileServerConfigBean.getFtp().get("password"),
                    fileServerConfigBean.getNginx().get("faviconPath"),
                    "",
                    faviconName,
                    faviconInputStream);
        }
        if (faviconName == null || faviconName.length() == 0) {
            faviconName = String.format("%08d.jpg", personId);
        }
        faviconUrl += "/" + faviconName;
        return faviconUrl;
    }

    private void updatePersonTable(Integer personId,
                                   String name,
                                   String faviconUrl) {
        // 添加对于数据库的判断
        // save person
        List<Person> persons = personService.getPersonByPersonId(personId);
        if (persons.size() > 0) {
            Person newPerson = persons.get(0);
            for (Person oldPerson : persons) {
                personService.delPerson(oldPerson.getId());
            }
            newPerson.setPersonId(personId);
            newPerson.setName(name);
            if (faviconUrl.substring(faviconUrl.length()-12)
                    != fileServerConfigBean.getNginx().get("faviconDefaultName")) {
                newPerson.setFaviconUrl(faviconUrl);
            }
            newPerson.setUpdateTime(System.currentTimeMillis());
            personService.addPerson(newPerson);

        } else {
            Person newPerson = new Person();
            newPerson.setPersonId(personId);
            newPerson.setName(name);
            newPerson.setFaviconUrl(faviconUrl);
            newPerson.setStatus(1);
            newPerson.setCreateTime(System.currentTimeMillis());
            newPerson.setUpdateTime(System.currentTimeMillis());
            personService.addPerson(newPerson);
        }
    }

    private void updateFaceTable(Integer personId,
                                 String cameraId) {
        List<Face> oldFaces = faceService.getFaceByPersonIdAndCameraId(personId, cameraId);
        for (Face oldFace : oldFaces) {
            faceService.delFace(oldFace.getId());
        }
    }

    public void saveFace(Integer personId,
                         String cameraId,
                         Face face) {

        face.setPersonId(personId);
        face.setCameraId(cameraId);

        String faceSubdir = personId + "/" + cameraId;
        String faceImageName = String.format("%08d_%s_%d_%04d.jpg",
                personId,
                cameraId,
                System.currentTimeMillis(),
                (int)(Math.random()*10000));
        String faceUrl = fileServerConfigBean.getNginx().get("faceUrl") + "/" + faceSubdir + "/" + faceImageName;
        face.setImageUrl(faceUrl);

        face.setStatus(1);
        face.setCreateTime(System.currentTimeMillis());
        face.setUpdateTime(System.currentTimeMillis());
        faceService.addFace(face);

        // save face image
        String imageBase64 = face.getImageBase64();
        Mat faceMat = ImgUtils.byteArrBase64ToMat(imageBase64);
        byte[] faceByteArr = ImgUtils.mat2ByteArr(faceMat);
        InputStream faceInputStream = ImgUtils.byteArr2InputStream(faceByteArr);
        FtpUtils.uploadFile(
                fileServerConfigBean.getIp(),
                Integer.parseInt(fileServerConfigBean.getFtp().get("port")),
                fileServerConfigBean.getFtp().get("username"),
                fileServerConfigBean.getFtp().get("password"),
                fileServerConfigBean.getNginx().get("facePath"),
                faceSubdir,
                faceImageName,
                faceInputStream);
    }

/**********************************************************************************************************************/

//    @PostMapping("/save/person")
//    public FrontResult savePerson(@RequestBody String body) throws IOException {
//
//        JSONObject jsonObject = JSON.parseObject(body);
//
//        Integer personId = jsonObject.getInteger("person_id");
//        String name = jsonObject.getString("name");
//        personService.addPerson(personId, name);
//
//        String data = jsonObject.getString("data");
//        FrontResult result = new FrontResult();
//        try {
//            RecogData saveData = mapper.readValue(data, RecogData.class);
//            List<Face> faces = saveData.getFace();
//            for (Face face : faces) {
//                List<Float> feature = face.getFeature();
//                if (feature == null) continue;
//                String featureJson = mapper.writeValueAsString(feature);
//                faceService.addFace(personId, featureJson);
//            }
//            result.setCode(200);
//            result.setMsg("SUCCESS.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            result.setCode(500);
//            result.setMsg("FAIL.");
//            return result;
//        }
//        return result;
//    }


}
