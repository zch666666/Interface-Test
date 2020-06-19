package com.interfacetest.dataprovider;

import com.interfacetest.util.HttpUtil;

import com.interfacetest.util.PropertiesUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static com.interfacetest.util.FileUtil.writeJsonToFile;

/*
*get information form Swagger server & convert to json files.
*
 */
public class GetSwagger {
    private static final Logger logger = LoggerFactory.getLogger(GetSwagger.class);
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
        JSONObject param=new JSONObject();
        String result;
        result= HttpUtil.sendGet(host,param);
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

        public static void main(String[] args) {
        swaggerAllJsonStr=getSwaggerFromUrl(HOST);
        interfaceInfoJson = interfaceParser(swaggerAllJsonStr);
            System.out.println(interfaceInfoJson);
        paramsInfoJson = paramsInfoParser(swaggerAllJsonStr);
            System.out.println(paramsInfoJson);
        }
}
