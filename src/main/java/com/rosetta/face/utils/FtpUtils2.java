package com.rosetta.face.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpUtils2 {

    private  FTPClient ftp;

    public FtpUtils2(String addr, int port, String username, String password) throws IOException {
        ftp = new FTPClient();
        ftp.connect(addr,port);
        ftp.login(username,password);

        ftp.setControlEncoding("UTF-8");
        int reply;
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
        }
        ftp.enterLocalPassiveMode();
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
    }

    /**
     *
     * @param path 上传到ftp服务器哪个路径下
     * @return
     * @throws Exception
     */
    public void cd(String path) throws Exception {
        ftp.changeWorkingDirectory(path);
    }

    public boolean mkdir(String path) throws IOException {
        return ftp.makeDirectory(path);
    }

    /**
     *
     * @param file 上传的文件或文件夹
     * @throws Exception
     */
    public void upload(File file) {

        try {
            if(file.isDirectory()){
                ftp.makeDirectory(file.getName());
                ftp.changeWorkingDirectory(file.getName());
                String[] files = file.list();
                for (int i = 0; i < files.length; i++) {
                    File file1 = new File(file.getPath()+"\\"+files[i] );
                    if(file1.isDirectory()){
                        upload(file1);
                        ftp.changeToParentDirectory();
                    }else{
                        File file2 = new File(file.getPath()+"\\"+files[i]);
                        FileInputStream input = new FileInputStream(file2);
                        ftp.storeFile(file.getName(), input);
                        input.close();
                    }
                }
            }else{
                File file2 = new File(file.getPath());
                FileInputStream input = new FileInputStream(file2);
                boolean result = ftp.storeFile(file.getName(), input);
                System.out.println(result);
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void destroy() throws IOException {
        if(ftp != null){
            ftp.disconnect();
            ftp = null;
        }
    }

    public static void main(String[] args) throws Exception{
        FtpUtils2 t = new FtpUtils2("172.18.7.208", 21, "lab", "lab");
        boolean isDirectory = t.mkdir("/disk03/test");
        System.out.println("创建目录是否成功： ======================" + isDirectory);
        t.cd("/disk03/test");
        File file = new File("/home/czj/3-data/test/jpg/01.jpg");
        t.upload(file);
        t.destroy();
    }

}
