package com.interfacetest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void writeJsonToFile(String JsonStr,String FileName) {
        try{
            FileWriter fw = new FileWriter(new File(System.getProperty("user.dir") +PropertiesUtil.getProperty("jsonFilesPath")+FileName+".json"));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(JsonStr);
            bw.flush();
            bw.close();}
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
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isFile()) {
                    jsonNodeMap.put(tempList[i].getName().replace(".json",""),objectMapper.readTree(tempList[i]));
                }
                if (tempList[i].isDirectory()) {
                    //这里就不递归了，
                }
            }
            return jsonNodeMap;
        } catch (IOException e){
            logger.info("Exception occurred in getFiles.", e);
        }
        return jsonNodeMap;
    }

}
