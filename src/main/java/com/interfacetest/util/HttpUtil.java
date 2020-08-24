package com.interfacetest.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static HttpResponse sendGet(String url, String jsonRequest){
        HttpGet httpGet;
        if (jsonRequest==null||jsonRequest.isEmpty()||jsonRequest.equals("{}")){
            logger.info("This test without any param");
            httpGet=new HttpGet(url);
        }
        else {
            JSONObject json=new JSONObject(jsonRequest);
            //convert json into params
            String key;
            String value;
            StringBuilder sb=new StringBuilder();
            Iterator<?> it=json.keys();
            while(it.hasNext()){
                key=it.next().toString();
                value=  json.get(key).toString();
                sb.append(key).append("=").append(value).append("&");
            }
            String params=sb.deleteCharAt(sb.length()-1).toString();
            logger.info("request data is "+"\t\n"+params);
            httpGet=new HttpGet(url+"?"+params);
        }
        HttpResponse response=null;
        try{
            response=getHttpclient().execute(httpGet);
        }catch (IOException e1){
            e1.printStackTrace();
        }
        return response;
    }

    public static HttpResponse sendPost(String url,String jsonRequest){
        int statusCode;
        HttpPost post=new HttpPost(url);
        post.addHeader("Content-Type","application/json; charset=utf-8");
        post.addHeader("Accept","application/json");
        if (!jsonRequest.isEmpty())
        post.setEntity(new StringEntity(jsonRequest, "utf-8"));

        HttpResponse response=null;
        try{
            response=getHttpclient().execute(post);
            statusCode=response.getStatusLine().getStatusCode();
            if (statusCode!= 200){
                logger.error("Method failed:"+response.getStatusLine());
            }
        }catch (IOException e){
            logger.error("exception: "+e);
        }
        return response;
    }

    public static HttpResponse sendPatch(String url,String jsonRequest){
        JSONObject json=new JSONObject(jsonRequest);
        int statusCode;
        HttpPatch patch=new HttpPatch(url);
        patch.setHeader("Content-type", "application/json");
        patch.setHeader("Charset", "UTF-8");
        patch.setHeader("Accept", "application/json");
        patch.setHeader("Accept-Charset", "UTF-8");

        HttpResponse response=null;
        try{
            if (!json.isEmpty())
            {
                StringEntity entity = new StringEntity(json.toString(),"utf-8");
                patch.setEntity(entity);
            }
            response=getHttpclient().execute(patch);
            statusCode=response.getStatusLine().getStatusCode();
            if (statusCode!= 200){
                logger.error("Method failed:"+response.getStatusLine());
            }
        }catch (IOException e){
            logger.error("exception: "+e);
        }
        return response;
    }

    public static HttpResponse sendPut(String url,String jsonRequest){
        JSONObject json=new JSONObject(jsonRequest);
        int statusCode;
        HttpPut httpPut=new HttpPut(url);
        httpPut.setHeader("Content-type", "application/json");
        HttpResponse response=null;
        try{
            if (!json.isEmpty())
            {
                StringEntity entity = new StringEntity(json.toString(),"utf-8");
                httpPut.setEntity(entity);
            }
            response=getHttpclient().execute(httpPut);
            statusCode=response.getStatusLine().getStatusCode();
            if (statusCode!= 200){
                logger.error("Method failed:"+response.getStatusLine());
            }
        }catch (IOException e){
            logger.error("exception: "+e);
        }
        return response;
    }

    private static CloseableHttpClient getHttpclient() {
        return HttpClients.createDefault();
    }

//    public static String getToken() throws JSONException {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.put("appKey", Arrays.asList(JournalismUtil.JOURNALISMTOKENAPPKEY));
//        params.put("secret", Arrays.asList(JournalismUtil.JOURNALISMTOKENSECRET));
//        String token = HttpClient.sendPOSTRequest(JournalismUtil.JOURNALISMTOKENURL, params);
//        JSONObject jsonObj = new JSONObject(token);
//        String Authorization=  = jsonObj.getString("token");
//        return Authorization;
//    }

}
