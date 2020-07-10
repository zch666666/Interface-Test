package com.interfacetest.dataprovider;

import com.interfacetest.util.HttpUtil;

import com.interfacetest.util.PropertiesUtil;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import static com.interfacetest.util.FileUtil.writeJsonToFile;

/*
*get information form Swagger server & convert to json files.
*
 */
public class GetSwagger {
    //host format : http://localhost/8080
    //private static String HOST=System.getProperty("host")+"/v2/api-docs";
    private static String HOST= PropertiesUtil.getProperty("host")+"/v2/api-docs";
    private static String swaggerAllJsonStr;
    private static JSONObject interfaceInfoJson;
    private static JSONObject paramsInfoJson;

    //get from swagger & split into interface json and params json.
    public static void run()  {
        swaggerAllJsonStr=getSwaggerFromUrl(HOST);
        interfaceInfoJson = interfaceParser(swaggerAllJsonStr);
        paramsInfoJson = paramsInfoParser(swaggerAllJsonStr);
    }

    public static String getSwaggerFromUrl(String host) {
        String result=null;
        HttpEntity entity=HttpUtil.sendGet(host, null).getEntity();
        if (entity!=null){
            try {
                result= EntityUtils.toString(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (result != null){
            writeJsonToFile(result,"swaggerAll");
            return result;
        }
        return null;
    }

    //Extract interfaces information from swagger json to file
    private static JSONObject interfaceParser(String swaggerJson) {
        JSONObject jsonObject=new JSONObject(swaggerJson);
        JSONObject paths=jsonObject.getJSONObject("paths");
        writeJsonToFile(paths.toString(),"allInterfaces");
        for (Iterator<String> it = paths.keys(); it.hasNext(); ) {
            String key = it.next();
            if ("/error".equals(key)) it.remove();
            else{
                JSONObject path=paths.getJSONObject(key);
                String keyStr=key.replace("/","-");
                writeJsonToFile(path.toString(),"interfaces\\"+keyStr);
            }
        }
        return paths;
    }

    //Extract params information from swagger json
    private static JSONObject paramsInfoParser(String swaggerJson) {
        JSONObject jsonObject=new JSONObject(swaggerJson);
        JSONObject params=jsonObject.getJSONObject("definitions");
        writeJsonToFile(params.toString(),"allModels");
        for (Iterator<String> it = params.keys(); it.hasNext(); ) {
            String key = it.next();
            if ("ModelAndView".equals(key)||"View".equals(key))
                it.remove();
            else{
                JSONObject param=params.getJSONObject(key);
                writeJsonToFile(param.toString(),"models\\"+key);
            }
        }
        return params;
    }
}
