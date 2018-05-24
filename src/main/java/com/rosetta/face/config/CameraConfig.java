package com.rosetta.face.config;

import com.rosetta.face.config.bean.CameraConfigBean;
import com.rosetta.face.service.Camera;
import com.rosetta.face.service.CameraService;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// just for test
// hahahah

@Configuration
@EnableWebSocketMessageBroker
public class CameraConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/camera");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/video/api/websocket/preview").setAllowedOrigins("*").withSockJS();
    }

    /******************************************************************************************************************/

    @Autowired
    private CameraConfigBean cameraConfigBean;

    @Resource(name = "exe")
    private TaskExecutor executor;

    @Bean
    public CameraService initCamera() {
        List<Map<String, String>> cameraConfigList = cameraConfigBean.getList();
        Map<String, Camera> cameras = new HashMap<String, Camera>();
        for (Map<String, String> cameraConfigMap : cameraConfigList) {
            String cameraId = cameraConfigMap.get("id");
            cameraId = String.format("%02d", Integer.parseInt(cameraId));
            String cameraUrl = cameraConfigMap.get("url");
            cameras.put(cameraId, new Camera(cameraId, cameraUrl));
        }
        System.out.println("CameraConfig.initCamera: cameras.size=" + cameras.size());
        return new CameraService(executor, cameras);
    }

}
