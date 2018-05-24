package com.rosetta.face.config.bean;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Test {

    @Autowired
    private FileServerConfigBean fileServerConfigBean;

//    @Scheduled(fixedRate = 500)
    public void man() {

        String ip = fileServerConfigBean.getIp();

        System.out.println("-----------------------------" + JSON.toJSONString(fileServerConfigBean));
    }
}
