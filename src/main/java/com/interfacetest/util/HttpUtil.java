package com.interfacetest.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static String sendGet(String url, JSONObject json){
        HttpGet httpGet;
        if (json.isEmpty()){
            logger.info("This test without any param");
            httpGet=new HttpGet(url);
        }
        else {
            //convert json into params
            String key;
            String value;
            StringBuilder sb=new StringBuilder();
            Iterator<?> it=json.keys();
            while(it.hasNext()){
                key=it.next().toString();
                value=json.getString(key);
                sb.append(key).append("=").append(value).append("&");
            }
            String params=sb.deleteCharAt(sb.length()-1).toString();
            logger.info("request data is "+"\t\n"+params);
            httpGet=new HttpGet(url+"?"+params);
        }
        CloseableHttpResponse response=null;
        try{
            response=getHttpclient().execute(httpGet);
        }catch (IOException e1){
            e1.printStackTrace();
        }
        String result=null;
        try{
            HttpEntity entity=response.getEntity();
            if (entity!=null){
                result= EntityUtils.toString(entity);
            }
        }catch (ParseException|IOException e){
            e.printStackTrace();
        }finally {
            try {
                response.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String sendPost(String url,String jsonRequest){
        String body=null;
        int statusCode;
        HttpPost post=new HttpPost(url);
        post.addHeader("Content-Type","application/json; charset=utf-8");
        post.addHeader("Accept","application/json");
        post.setEntity(new StringEntity(jsonRequest, Charset.forName("UTF-8")));

        HttpResponse response;
        try{
            response=getHttpclient().execute(post);
            statusCode=response.getStatusLine().getStatusCode();
            if (statusCode!= 200){
                logger.error("Method failed:"+response.getStatusLine());
            }
            body=EntityUtils.toString(response.getEntity(),"UTF-8");
        }catch (IOException e){
            logger.error("exception: "+e);
        }
        return body;
    }

    private static CloseableHttpClient getHttpclient() {
        return HttpClients.createDefault();
    }

}
