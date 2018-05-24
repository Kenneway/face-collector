package com.rosetta.face.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class VideoListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(VideoListener.class);



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        try {
            logger.info("[ROSETTA] Attendancer start up.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}



