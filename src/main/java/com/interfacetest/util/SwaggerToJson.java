package com.interfacetest.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class SwaggerToJson {
    private static final Logger logger = LoggerFactory.getLogger(SwaggerToJson.class);
    //host format http://localhost/9999
    private static String host=System.getProperty("host")+"/v2/api-docs";
    private static String swaggerAllJsonStr;
    private static JSONObject interfaceInfoJson;
    private static JSONObject paramsInfoJson;

    public static void run()  {
        swaggerAllJsonStr = getSwaggerFromUrl(host);
        interfaceInfoJson = interfaceParser(swaggerAllJsonStr);
        paramsInfoJson = paramsInfoParser(swaggerAllJsonStr);
    }

    public static String getSwaggerFromUrl(String host) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(host);
            //Max Connecting Time is 30s
            RequestConfig config = RequestConfig.custom().setConnectTimeout(30000).build();
            httpGet.setConfig(config);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response != null){
                HttpEntity entity =  response.getEntity();  //获取网页内容
                String result = EntityUtils.toString(entity, "UTF-8");
                writeJsonToFile(result,"swaggerAll.json");
                return result;
            }
        }catch (IOException e){
            logger.info("Exception occured in getSwaggerJson.", e);
        }
        return null;
    }

    //Extract interfaces information from swagger json
    private static JSONObject interfaceParser(String swaggerJson) {
        JSONObject jsonObject=new JSONObject(swaggerJson);
        JSONObject paths=jsonObject.getJSONObject("paths");
        writeJsonToFile(paths.toString(),"interfaces\\"+"allInterfaces.json");
        return paths;
}

    //Extract params information from swagger json
    private static JSONObject paramsInfoParser(String swaggerJson) {
        JSONObject jsonObject=new JSONObject(swaggerJson);
        JSONObject params=jsonObject.getJSONObject("definitions");
        writeJsonToFile(params.toString(),"models\\"+"allModels.json");
        return params;
    }

        public static void main(String[] args) {
        swaggerAllJsonStr=getSwaggerFromUrl("http://localhost:9999/v2/api-docs");
        interfaceInfoJson = interfaceParser(swaggerAllJsonStr);
            System.out.println(interfaceInfoJson);
        paramsInfoJson = paramsInfoParser(swaggerAllJsonStr);
            System.out.println(paramsInfoJson);

        }

    private static void writeJsonToFile(String JsonStr,String FileName) {
        try{
            FileWriter fw = new FileWriter(new File(System.getProperty("user.dir") +"\\src\\main\\resources\\jsonfile\\"+FileName));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(JsonStr);
            bw.flush();
            bw.close();}
        catch (IOException e){
            logger.info("Exception occured in writeJsonToFile.", e);
        }
    }
}
