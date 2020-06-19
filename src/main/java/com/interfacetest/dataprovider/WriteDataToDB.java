package com.interfacetest.dataprovider;

import com.fasterxml.jackson.databind.JsonNode;
import com.interfacetest.util.FileUtil;
import com.interfacetest.util.ParamParser;
import com.interfacetest.util.PropertiesUtil;

import java.io.IOException;
import java.util.Map;

public class WriteDataToDB {
    /**
     * 将接口测试用例信息写入数据库，
     * 需要将参数从swagger形式转换成json形式
     */
    public static void run() throws IOException {
        JsonNode params;
        //读取所有interfaces中.json文件转成JsonNode
        //key 为请求uri, value 是请求其他参数信息。
        Map<String, JsonNode> interfacesSwaggerMap= FileUtil.getFilesToMap(System.getProperty("user.dir") + PropertiesUtil.getProperty("jsonFilesPath.interfaces"));

        for (Map.Entry<String, JsonNode> entry : interfacesSwaggerMap.entrySet()){
            //请求参数格式转换，从swagger转换成可以直接请求的参数
            params=ParamParser.interfaceParamParser(entry.getValue());

            //写库

            //接口地址，请求信息，参数信息，等等等。
        }
    }
}
