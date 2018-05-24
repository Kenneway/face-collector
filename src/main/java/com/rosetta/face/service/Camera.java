package com.rosetta.face.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.util.Base64;

import static org.opencv.highgui.Highgui.CV_CAP_PROP_BUFFERSIZE;

public class Camera implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Camera.class);

    @Autowired
    private ObjectMapper mapper;

    private String id;

    private String streamUrl;

    private VideoCapture capture;

//    private TaskExecutor executor;

    private volatile Mat lastFrame = new Mat();

    public Camera(String id, String streamUrl) {
        this.id = id;
        this.streamUrl = streamUrl;
//        this.executor = executor;
        this.capture = new VideoCapture(streamUrl);
//        this.capture.set(CV_CAP_PROP_BUFFERSIZE, 3);

//        executor.execute(this);
    }

    @Override
    public void run() {
        logger.info("Camera [" + id + "] start up.");
//        int i = 0;

        while (capture.isOpened()) {
            double v = this.capture.get(CV_CAP_PROP_BUFFERSIZE);
            System.out.println("===================" + v + "===============");
            Mat tmp = new Mat();
            boolean res = capture.read(tmp);
            synchronized (this) {
                tmp.copyTo(lastFrame);
            }
            tmp.release();
//            if ( 0 == i % 3) {
//                //detectLast face.
//                new Runnable(){
//                    public void run() {
//
//                    }
//                }.run();
//            }
//
//            i = (++i) % 3;
        }

    }

    public Mat getLastFrame(){
        return lastFrame;
    }

    public byte[] getImage() {

        Mat frame = new Mat();
//        Imgproc.resize(lastFrame,frame,new Size(640, 480));

        Mat tmp = new Mat();
        synchronized (this) {
            if (lastFrame != null) {
                lastFrame.copyTo(tmp);
            }
        }
        Imgproc.resize(tmp,frame,new Size(480, 360));
//        frame = tmp;
        MatOfByte buffer = new MatOfByte();
        Highgui.imencode(".jpg", frame, buffer);
        tmp.release();
        frame.release();
        byte[] imgBytes = Base64.getEncoder().encode(buffer.toArray());
        buffer.release();
        return imgBytes;
    }

}
