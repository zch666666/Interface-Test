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
        String parameters="";
        if (apiData.getParameters()!=null)
            parameters=apiData.getParameters();

        String url=apiData.getUrl();
        HttpResponse response;
        int statusCode;
        //Call different request functions according to different request methods
        switch (method){
            case "get":
                response=HttpUtil.sendGet(url,parameters);
                break;
            case "post":
                response=HttpUtil.sendPost(url,parameters);
                break;
            case "patch":
                response=HttpUtil.sendPatch(url,parameters);
                break;
            case "put":
                response=HttpUtil.sendPut(url,parameters);
                break;
            default:
                response=HttpUtil.sendGet(url,parameters);
                   break;
        }

        statusCode=response.getStatusLine().getStatusCode();
        apiData.setStatusCode(statusCode);
        sqlSession.update("updateRunStatues",apiData);
        sqlSession.update("updateStatuesCode",apiData);
        sqlSession.commit();

        Assert.assertEquals(statusCode,200,url+",接口访问失败！");
    }

}
