package com.interfacetest.dataprovider;

import com.fasterxml.jackson.databind.JsonNode;
import com.interfacetest.entity.ApiData;
import com.interfacetest.util.DBUtil;
import com.interfacetest.util.FileUtil;
import com.interfacetest.util.ParamParser;
import com.interfacetest.util.PropertiesUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.*;

//读取本地Json文件,转换成测试用例存入数据库。
public class WriteDataToDB {
    private static final Logger logger = LoggerFactory.getLogger(GetSwagger.class);
    private static Set<ApiData> apiDataBeans;

    //initial test cases Bean
    static {
        apiDataBeans =new HashSet<>();
        setApiCases();
    }
    /**
     * 将接口测试用例信息写入数据库，
     */
    public static void run(){
        SqlSession sqlSession= DBUtil.getSession();
        for (ApiData apiCase: apiDataBeans) {
            sqlSession.insert("ins",apiCase);
        }
        sqlSession.commit();
        DBUtil.close();
    }

    /**
     * 读取本地文件并转换成bean样式
     * 读取参数形式:GetSwagger.run()生成的本地json文件
     * 输出:Set<ApiDataBean> apiCases
     */
    public static void setApiCases()  {
        JsonNode params;
        //读取所有interfaces中.json文件转成JsonNode，再转换成ApiData以Bean的形式存入数据库
        //key 为请求uri, value是请求其他参数信息。
        Map<String, JsonNode> interfacesSwaggerMap= FileUtil.getFilesToMap(System.getProperty("user.dir") + PropertiesUtil.getProperty("jsonFilesPath.interfaces"));
        for (Map.Entry<String, JsonNode> entry : interfacesSwaggerMap.entrySet()){
            //请求参数格式转换，从swagger转换成可以直接请求的参数
            try {
                //注：目前仅支持生成可以访问的随机数据，需要在后续开发加入填充有效数据的解析方法
                //ParamParser.interfaceParamParser()可以做继续开发，目标生成多个有效参数--2020.7.2
                params=ParamParser.interfaceParamParser(entry.getValue());
                String method="";
                JsonNode content=entry.getValue();
                Iterator<String> keys = content.fieldNames();
                while(keys.hasNext()){
                    method = keys.next();
                    //System.out.println("key键是:"+url);
                }
                ApiData apiData =creatBean(PropertiesUtil.getProperty("host")+entry.getKey(),method,params,content.get(method));
                apiDataBeans.add(apiData);
            } catch (Exception e) {
                logger.info(entry.getKey()+":"+e);
            }
        }
    }

    private static ApiData creatBean(String url, String method, JsonNode params, JsonNode content) {
        ApiData apiData =new ApiData();
        apiData.setUrl(url.replace("-","/"));
        apiData.setMethod(method);
        apiData.setStatusCode(404);
        apiData.setRun(false);
        apiData.setCreateTime(new Timestamp(new Date().getTime()));
        if (!params.isNull()||!params.isEmpty())
            apiData.setParameters(params.toString());
        if (content.has("summary"))
            apiData.setSummary(content.get("summary").toString());
        if (content.has("produces"))
            apiData.setProduces(content.get("produces").toString());
        if (content.has("operationId"))
            apiData.setOperationId(content.get("operationId").toString());
        if (content.has("consumes"))
            apiData.setConsumes(content.get("consumes").toString());
        if (content.has("tags"))
            apiData.setTags(content.get("tags").toString());
        return apiData;
    }

}
