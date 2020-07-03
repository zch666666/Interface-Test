package com.interfacetest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    //create work dir
    static {
        File jsonDir= new File(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath"));
        if(!jsonDir.exists()&& !jsonDir.isDirectory()){
            jsonDir.mkdir();
        }
        File paramsDir= new File(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath.interfaces"));
        if(!paramsDir.exists()&& !paramsDir.isDirectory()){
            paramsDir.mkdir();
        }
        File interfacesDir= new File(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath.models"));
        if(!interfacesDir.exists()&& !interfacesDir.isDirectory()){
            interfacesDir.mkdir();
        }
    }

    public static void writeJsonToFile(String JsonStr,String FileName) {
        try{
            FileOutputStream fos=new FileOutputStream(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath")+FileName+".json");
            OutputStreamWriter  fw = new OutputStreamWriter(fos, "UTF-8");
            fw.write(JsonStr);
            fw.flush();
            fw.close();}
        catch (IOException e){
            logger.info("Exception occurred in writeJsonToFile.", e);
        }
    }

    public static JsonNode getInterfaceJson(String FileName) {
        ObjectMapper objectMapper=new ObjectMapper();
        File file=new File(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath.interfaces")+FileName+".json");
        try {
            JsonNode root=objectMapper.readTree(file);
            String filedName=root.fieldNames().next();
            return root.path(filedName);
        } catch (IOException e){
            logger.info("Exception occurred in getInterfaceJson.", e);
        }
        return null;
    }

    public static JsonNode getParamJson(String paramFileName) {
        ObjectMapper objectMapper=new ObjectMapper();
        File file=new File(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath.models")+paramFileName+".json");
        try {
            return objectMapper.readTree(file);
        } catch (IOException e){
            logger.info("Exception occurred in getParamJson.", e);
        }
        return null;
    }

    //获取目录下所有文件转成Map
    public static Map<String,JsonNode> getFilesToMap(String path){
        ObjectMapper objectMapper=new ObjectMapper();
        Map<String,JsonNode> jsonNodeMap=new HashMap<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        try {
            for (File tempFile:tempList) {
                if (tempFile.isFile()) {
                    String fileName=tempFile.getName().replace(".json","");
                    jsonNodeMap.put(fileName,objectMapper.readTree(tempFile));
                }
                if (tempFile.isDirectory()) {
                    //这里就不递归了，
                }
            }
            return jsonNodeMap;
        } catch (IOException e){
            logger.info("Exception occurred in getFiles."+e.getClass());
        }
        return jsonNodeMap;
    }

}
