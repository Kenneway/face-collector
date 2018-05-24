package com.rosetta.face.service;


import com.rosetta.face.config.bean.FileServerConfigBean;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

//@Service
public class FTPService {

    private FTPClient ftpClient = null;

//    @Autowired
    public FTPService(FileServerConfigBean fileServerConfigBean){
        ftpClient = new FTPClient();
        try {
            // 连接FTP服务器
            ftpClient.connect(fileServerConfigBean.getIp(), Integer.parseInt(fileServerConfigBean.getFtp().get("port")));
            // 登录FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftpClient.login(fileServerConfigBean.getFtp().get("username"), fileServerConfigBean.getFtp().get("password"));
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                close();
                throw new IOException("连接FTP服务器失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FTPService(String host,
                      int port,
                      String username,
                      String password){
        ftpClient = new FTPClient();
        try {
            // 连接FTP服务器
            ftpClient.connect(host, port);
            // 登录FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftpClient.login(username, password);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                close();
                throw new IOException("连接FTP服务器失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            if(ftpClient != null){
                if(ftpClient.isConnected()){
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
                ftpClient = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean uploadFile(String basePath,
                              String filePath,
                              String filename,
                              InputStream input) {
        boolean result = false;
        try {
            //切换到上传目录
            if (!ftpClient.changeWorkingDirectory(basePath + "/" + filePath)) {
                //如果目录不存在创建目录
                String[] dirs = filePath.split("/");
                String tempPath = basePath;
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) continue;
                    tempPath += "/" + dir;
                    if (!ftpClient.changeWorkingDirectory(tempPath)) {
                        if (!ftpClient.makeDirectory(tempPath)) {
                            return result;
                        } else {
                            ftpClient.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            //设置上传文件的类型为二进制类型
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //上传文件
            if (!ftpClient.storeFile(filename, input)) {
                return result;
            }
            input.close();
            ftpClient.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

//    public boolean mkdir(String dir){
//        if (dir == null || dir.length() == 0) return false;
//        try {
//            if (ftpClient.changeWorkingDirectory(dir)) {
//                return true;
//            } else {
//                ftpClient.changeToParentDirectory();
//                ftpClient.makeDirectory(dir);
//            }
//
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//    }

//    public boolean mkdirs(String dir){
//        String[] dirs = dir.split("/");
//        if(dirs.length == 0){
//            return false;
//        }
//        boolean stat = false;
//        try {
//            ftpClient.changeToParentDirectory();
//            for(String dirss : dirs){
//                ftpClient.makeDirectory(dirss);
//                ftpClient.changeWorkingDirectory(dirss);
//            }
//
//            ftpClient.changeToParentDirectory();
//            stat = true;
//        } catch (IOException e) {
//            stat = false;
//        }
//        return stat;
//    }

    public boolean rmdirs(String dirPath){
        try{
            if (ftpClient.changeWorkingDirectory(dirPath)) {
                FTPFile[] ftpFiles = ftpClient.listFiles();
                for(FTPFile file : ftpFiles){
                    String filePath = dirPath + "/" + file.getName();
                    if(file.isDirectory()){
                        rmdirs(filePath);
                    } else {
                        ftpClient.deleteFile(filePath);
                    }
                }
                ftpClient.changeToParentDirectory();
                String dirName = dirPath.substring(dirPath.lastIndexOf("/") + 1);
                ftpClient.removeDirectory(dirName);
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        FTPService ftpService = new FTPService("172.18.2.107",
                21,
                "lab",
                "lab");
//        ftpService.rmdirs("/data/test");
        File inputFile = new File("/home/czj/3-data/test/jpg/11.jpg");
        ftpService.uploadFile("/data/test",
                "01/12",
                "7.jpg",
                new FileInputStream(inputFile));

    }


//    public boolean remove(String pathname){
//        try{
//            return ftpClient.deleteFile(pathname);
//        }catch(Exception e){
//            return false;
//        }
//    }
//
//
//    public boolean rename(String pathname1,String pathname2){
//        try {
//            return ftpClient.rename(pathname1, pathname2);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }







}
