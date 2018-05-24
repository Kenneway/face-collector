package com.rosetta.face.controller;

import com.rosetta.face.config.bean.CameraConfigBean;
import com.rosetta.face.service.Camera;
import com.rosetta.face.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableAsync
@EnableScheduling
@Controller
public class CameraController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private CameraConfigBean cameraConfigBean;

    @Async
    @Scheduled(fixedRate = 50)
    public void sendVideoStream() throws Exception {

        List<Map<String, String>> cameraConfigList = cameraConfigBean.getList();
        for (Map<String, String> cameraConfigMap : cameraConfigList) {
            String cameraId = cameraConfigMap.get("id");
            cameraId = String.format("%02d", Integer.parseInt(cameraId));
            Camera camera = cameraService.getCameraById(cameraId);
            if (camera != null) {
                byte[] image = camera.getImage();
                template.convertAndSend("/camera/" + cameraId, new String(image));
            }


        }
    }

}
