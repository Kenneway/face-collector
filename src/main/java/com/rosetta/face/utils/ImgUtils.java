package com.rosetta.face.utils;

import org.apache.commons.codec.binary.Base64;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import java.io.*;

public class ImgUtils {

    public static byte[] encodeBase64(byte[] srcByteArr) {
        return (byte[]) Base64.encodeBase64(srcByteArr);
    }

    public static byte[] decodeBase64(byte[] srcByteArr) {
        return Base64.decodeBase64(srcByteArr);
    }

    public static byte[] mat2ByteArr(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Highgui.imencode(".jpg", frame, buffer);
        byte[] ImgByteArr = buffer.toArray();
        buffer.release();
        return ImgByteArr;
    }

    public static Mat byteArr2Mat(byte[] imgByteArr) {
        Mat encoded = new Mat(1, imgByteArr.length, CvType.CV_8U);
        encoded.put(0, 0, imgByteArr);
        Mat bgrMat = Highgui.imdecode(encoded, Highgui.CV_LOAD_IMAGE_COLOR);
        encoded.release();
        return bgrMat;
    }

    public static String matToByteArrBase64(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        Highgui.imencode(".jpg", frame, buffer);
//        return Base64.getEncoder().encode(buffer.toArray());
        byte[] imgByteArr = (byte[]) Base64.encodeBase64(buffer.toArray());
        buffer.release();
        return new String(imgByteArr);
    }

    public static Mat byteArrBase64ToMat(String imgStrBase64) {
        byte[] imgByteArr = Base64.decodeBase64(imgStrBase64.getBytes());
        Mat encoded = new Mat(1, imgByteArr.length, CvType.CV_8U);
        encoded.put(0, 0, imgByteArr);
        Mat bgrMat = Highgui.imdecode(encoded, Highgui.CV_LOAD_IMAGE_COLOR);
        encoded.release();
        return bgrMat;
    }

    public static Mat inputStreamIntoMat(InputStream inputStream) throws IOException {
        // Read into byte-array
        byte[] temporaryImageInMemory = inputStream2ByteArr(inputStream);

        // Decode into mat. Use any IMREAD_ option that describes your image appropriately
        Mat outputImage = Highgui.imdecode(new MatOfByte(temporaryImageInMemory), Highgui.IMREAD_COLOR);

        return outputImage;
    }

    public static byte[] inputStream2ByteArr(InputStream stream) throws IOException {
        // Copy content of the image to byte-array
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] temporaryImageInMemory = buffer.toByteArray();
        buffer.close();
        stream.close();
        return temporaryImageInMemory;
    }

    public static InputStream byteArr2InputStream(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

}
