package com.rosetta.face.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    // 递归删除
    public static void deleteDirs(File dir){
        if ( ! dir.exists()) return;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(int i=0; i<files.length; i++) {
                deleteDirs(files[i]);
            }
        }
        dir.delete();
    }

    // 递归创建
    public static void makeDirs(File dir) {
        if (dir.exists()) {
            return;
        }  else {
            dir.mkdirs();
        }
    }

    /**
     * 获取路径下的所有文件/文件夹
     * @param dirPath 需要遍历的文件夹路径
     * @param isAddDir 是否将子文件夹的路径也添加到list集合中
     * @return
     */
    public static List<String> listAllFiles(String dirPath, boolean isAddDir) {
        List<String> list = new ArrayList<String>();
        File baseFile = new File(dirPath);
        if (baseFile.isFile() || !baseFile.exists()) {
            return list;
        }
        File[] files = baseFile.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if(isAddDir){
                    list.add(file.getAbsolutePath());
                }
                list.addAll(listAllFiles(file.getAbsolutePath(),isAddDir));
            } else {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }

    public static List<String> listAllFiles(String dirPath) {
        return listAllFiles(dirPath, false);
    }

    public static List<File> listAllFiles(File dir) {
        List<File> list = new ArrayList<File>();
        if (dir.isFile() || !dir.exists()) {
            return list;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                list.addAll(listAllFiles(file));
            } else {
                list.add(file);
            }
        }
        return list;
    }

}
