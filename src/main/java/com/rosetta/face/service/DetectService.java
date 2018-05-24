package com.rosetta.face.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosetta.face.domain.Face;
import com.rosetta.face.utils.ImgUtils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DetectService {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private Detector detector;

    @Autowired
    private CameraService cameraService;

    public List<Face> detectLast(String id) throws IOException, InterruptedException {
        Camera camera = cameraService.getCameraById(id);
        // Mat clone
        Mat lastFrame = camera.getLastFrame().clone(); // lastFrame
        List<Face> faces = detector.detectFace(lastFrame);
        List<Face> resFaces = new ArrayList<>();
        for (Face face : faces) {
            List<Integer> rectList = face.getRect();
            // if size < 100, do not return to front end.
            if (rectList.get(2) < 100 || rectList.get(3) < 100) continue;
            Rect rect = new Rect(rectList.get(0), rectList.get(1), rectList.get(2),rectList.get(3));
            // bigger size crop
            Rect newRect = resizeRect(rect, 3.0);
            Mat faceCropMat = cropImage(lastFrame, newRect);
            if (faceCropMat == null) continue;
            String faceImageBase64 = new String(ImgUtils.matToByteArrBase64(faceCropMat));
            face.setImageBase64(faceImageBase64);
            resFaces.add(face);
            faceCropMat.release(); // faceMat
        }
        System.out.println("DetectService.detectLast: " + resFaces.size());
        return resFaces;
    }

    public List<Face> detectImage(Mat imgMat) throws IOException, InterruptedException {

        List<Face> faces = detector.detectFace(imgMat);
        List<Face> resFaces = new ArrayList<>();
        for (Face face : faces) {
            try {
                List<Integer> rectList = face.getRect();
                // if size < 100, do not return to front end.
                if (rectList.get(2) < 100 || rectList.get(3) < 100) continue;
                Rect rect = new Rect(rectList.get(0), rectList.get(1), rectList.get(2), rectList.get(3));
                // bigger size crop
                Rect newRect = resizeRect(rect, 3.0);
                Mat faceCropMat = cropImage(imgMat, newRect);
                if (faceCropMat == null) continue;
                String faceImageBase64 = new String(ImgUtils.matToByteArrBase64(faceCropMat));
                face.setImageBase64(faceImageBase64);
                resFaces.add(face);
                faceCropMat.release(); // faceMat
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        System.out.println("DetectService.detectLast: " + resFaces.size());
        return resFaces;
    }

    // bigger size
    Rect resizeRect(Rect srcRect, double rate) {
        double w = srcRect.width * rate;
        double h = srcRect.height * rate;
        double x0 = srcRect.x + (srcRect.width - w) / 2;
        double y0 = srcRect.y + (srcRect.height- h) / 2;
        return new Rect((int)x0, (int)y0, (int)w, (int)h);
    }

    // crop image
    Mat cropImage(Mat srcImg, Rect roi) {

        int x0 = roi.x >= 0 ? roi.x : 0;
        int y0 = roi.y >= 0 ? roi.y : 0;
        int x1 = (int)(roi.br().x <= srcImg.cols() ? roi.br().x : srcImg.cols()) ;
        int y1 = (int)(roi.br().y <= srcImg.rows() ? roi.br().y : srcImg.rows()) ;
        if(x0 >= x1 || y0 >= y1) return null;

        Mat dstImg = new Mat(roi.height, roi.width, srcImg.type(), new Scalar(0,0,0));
        Rect srcZone  = new Rect(x0, y0, x1-x0, y1-y0);
        Rect dstZone = new Rect(srcZone.x-roi.x, srcZone.y-roi.y, srcZone.width, srcZone.height);
        Mat srcZoneMat = new Mat(srcImg, srcZone); // tmpFaceMat
        Mat dstZoneMat = new Mat(dstImg, dstZone);
        srcZoneMat.copyTo( dstZoneMat );
        srcImg.release();
        dstImg.release();
        srcZoneMat.release();
        return dstZoneMat;
    }

//    @Scheduled(fixedRate = 5000)
    public void test() throws IOException, InterruptedException {

        System.out.println("============== test detect ==============");
        Camera camera = cameraService.getCameraById("02");
        Mat lastFrame = camera.getLastFrame();
        List<Face> faces = detector.detectFace(lastFrame);
        String facejson = mapper.writeValueAsString(faces);
        System.out.println("face:" + facejson);
    }
}
