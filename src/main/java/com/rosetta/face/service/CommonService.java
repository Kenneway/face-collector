package com.rosetta.face.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableAsync
@EnableScheduling
@Service
public class CommonService {

    @Async
    @Scheduled(fixedRate = 20000)
    public void gc() {
        //告诉垃圾收集器打算进行垃圾收集，而垃圾收集器进不进行收集是不确定的
        System.gc();
        //强制调用已经失去引用的对象的finalize方法
        System.runFinalization();
        System.out.println("CommonService.gc: done");
    }



}
