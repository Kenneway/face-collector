package com.rosetta.face.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


public class CameraService {

    @Autowired
    private ObjectMapper mapper;

    private TaskExecutor executor;

    private volatile Map<String, Camera> cameras;

    public CameraService(TaskExecutor executor, Map<String, Camera> cameras) {
        this.executor = executor;
        this.cameras = cameras;
        //
        for (Map.Entry<String, Camera> entry : cameras.entrySet()) {
            executor.execute(entry.getValue());
        }
    }

    public Camera getCameraById(String id) {
        return cameras.get(id);
    }

    public Map<String, Camera> getCameras() {
        return cameras;
    }

}
