package com.interfacetest.verification;

import com.interfacetest.dataprovider.GetSwagger;
import com.interfacetest.dataprovider.TestDataProvider;
import com.interfacetest.entity.ApiData;
import com.interfacetest.util.DBUtil;
import com.interfacetest.util.HttpUtil;
import org.apache.http.HttpResponse;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class apiVerification {

    private static SqlSession sqlSession;
    private static final Logger logger = LoggerFactory.getLogger(GetSwagger.class);

    @BeforeTest
    public void init(){
        sqlSession= DBUtil.getSession();
    }

    @AfterTest
    public void cleanUp(){
        DBUtil.close();
    }

    @Test(dataProvider = "apiDataList", dataProviderClass = TestDataProvider.class)
    public void apiTest(ApiData apiData){
        //System.out.println(apiData.toString());
        logger.info("------------------test start --------------------");
        String method=apiData.getMethod();
        JSONObject jsonObject=null;
        HttpResponse response;
        int statusCode;
        if (!apiData.getParameters().isEmpty()){
            jsonObject=new JSONObject(apiData.getParameters());
            System.out.println(jsonObject);
        }
        //Call different request functions according to different request methods
        if (method.equals("get")){
             response=HttpUtil.sendGet(apiData.getUrl(),jsonObject);
             statusCode=response.getStatusLine().getStatusCode();
             Assert.assertEquals(200,statusCode,apiData.getUrl()+",接口访问失败！");
             apiData.setStatusCode(statusCode);
        }
        else if (method.equals("post")){
            response=HttpUtil.sendPost(apiData.getUrl(),apiData.getParameters());
            statusCode=response.getStatusLine().getStatusCode();
            Assert.assertEquals(200,statusCode,apiData.getUrl()+",接口访问失败！");
            apiData.setStatusCode(statusCode);
        }

        sqlSession.update("updateRunStatues",apiData);
        sqlSession.update("updateStatuesCode",apiData);
        sqlSession.commit();
    }

}
