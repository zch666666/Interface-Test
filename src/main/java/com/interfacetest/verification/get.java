//package testSysApi;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//
//import bean.ApiDataBean;
//import configs.apiConfigs;
//
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.ParseException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.*;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.dom4j.DocumentException;
//import org.testng.Assert;
//import org.testng.ITestContext;
//import org.testng.annotations.*;
//import org.testng.annotations.Optional;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//import java.nio.file.Paths;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.regex.Matcher;
//
//import listener.AutoTestListener;
//import listener.RetryListener;
//import testCase.TestBase;
//import utils.fileUtil;
//import utils.randomUtil;
//import utils.reportUtil;
//
//@Listeners({ AutoTestListener.class, RetryListener.class })
//public class customTest extends TestBase {
//
//    /**
//     * api请求跟路径
//     */
//    private static String rootUrl;
//
//    /**
//     * 跟路径是否以‘/’结尾
//     */
//    private static boolean rooUrlEndWithSlash = false;
//
//    /**
//     * 所有公共header，会在发送请求的时候添加到http header上
//     */
//    private static Header[] publicHeaders;
//    /**
//     * 是否使用form-data传参 会在post与put方法封装请求参数用到
//     */
//    private static boolean requestByFormData = false;
//
//    /**
//     * 配置
//     */
//    private static apiConfigs apiConfig;
//
//    /**
//     * 所有api测试用例数据
//     */
//    protected List<ApiDataBean> dataList = new ArrayList<ApiDataBean>();
//    private static HttpEntity httpEntity;
//    private static HttpClient client;
//
//    /**
//     * 初始化测试数据
//     *
//     * @throws Exception
//     */
//    @Parameters("envName")
//    @BeforeSuite
//    public void init(@Optional("config.xml") String envName) throws Exception {
//        String configFilePath = Paths.get(System.getProperty("user.dir")+"\\config\\", envName).toString();
//        reportUtil.log("api config path:" + configFilePath);
//        apiConfig = new apiConfigs(configFilePath);
//        // 获取基础数据
//        rootUrl = apiConfig.getRootUrl();
//        rooUrlEndWithSlash = rootUrl.endsWith("/");
//
//        // 读取 param，并将值保存到公共数据map
//        Map<String, String> params = apiConfig.getParams();
//        setSaveDatas(params);
//
//        //读取配置xml文件，将公共请求头进行设置
//        List<Header> headers = new ArrayList<Header>();
//        apiConfig.getHeaders().forEach((key, value) -> {
//            Header header = new BasicHeader(key, value);
//            if(!requestByFormData && key.equalsIgnoreCase("content-type") && value.toLowerCase().contains("form-data")){
//                requestByFormData=true;
//            }
//            headers.add(header);
//        });
//        publicHeaders = headers.toArray(new Header[headers.size()]);
//        //对HttpClient设置超时时间
//        RequestConfig reqCon = RequestConfig.custom()
//                .setConnectTimeout(60000)
//                .setConnectionRequestTimeout(60000)
//                .setSocketTimeout(60000).build();
//        client = HttpClients.custom().setDefaultRequestConfig(reqCon).build();
//    }
//
//    @Parameters({ "excelPath"})
//    @BeforeTest
//    public void readData(@Optional("case/test-data.xls") String excelPath,ITestContext context) throws DocumentException {
//        //获取xml中所有的参数
//        Map<String,String> xmlParam = context.getCurrentXmlTest().getAllParameters();
//        List<String> sheetsName = new ArrayList<String>();
//        /*
//         * 可以指定多个sheetName的名字来进行测试
//         * 形式如        <parameter name="sheetName1" value="User"></parameter>
//                            <parameter name="sheetName2" value="Product"></parameter>
//         *  这里如果不进行过滤，可以修改为默认进行所有sheet的测试
//         */
//        for(String s : xmlParam.keySet()) {
//            if(s.contains("sheetName")) {
//                sheetsName.add(xmlParam.get(s));
//            }
//        }
//        String[] sheets = sheetsName.toArray(new String[sheetsName.size()]);
//        dataList = readExcelData(ApiDataBean.class, excelPath.split(";"),sheets);
//    }
//
//    /**
//     * 过滤数据，run标记为Y的执行。
//     *
//     * @return
//     * @throws DocumentException
//     */
//    @DataProvider(name = "apiDatas")
//    public Iterator<Object[]> getApiData(ITestContext context)
//            throws DocumentException {
//        List<Object[]> dataProvider = new ArrayList<Object[]>();
//        for (ApiDataBean data : dataList) {
//            if (data.isRun()) {
//                dataProvider.add(new Object[] { data });
//            }
//        }
//        return dataProvider.iterator();
//    }
//
//    @Test(dataProvider = "apiDatas")
//    public void apiTest(ApiDataBean apiDataBean) throws Exception {
//        reportUtil.log("------------------test start --------------------");
//        if (apiDataBean.getSleep() > 0) {
//            // sleep休眠时间大于0的情况下进行暂停休眠
//            reportUtil.log(String.format("sleep %s seconds",
//                    apiDataBean.getSleep()));
//            Thread.sleep(apiDataBean.getSleep() * 1000);
//        }
//        String apiParam = buildRequestParam(apiDataBean);
//        //由于headers要入参，因此Excel的
//        String headers = buildRequestHeader(apiDataBean);
//        // 封装请求方法
//        HttpUriRequest method = parseHttpRequest(apiDataBean.getUrl(),
//                apiDataBean.getMethod(),headers,apiParam);
//        String responseData;
//        try {
//            //增加运行时间计算显示
//            LocalDateTime beginTime = LocalDateTime.now();
//            // 执行
//            HttpResponse response = client.execute(method);
//            Long timeConsuming = Duration.between(beginTime,LocalDateTime.now()).toMillis();
//            reportUtil.log("测试执行时间为：" + timeConsuming + "ms!");
//            int responseStatus = response.getStatusLine().getStatusCode();
//            reportUtil.log("返回状态码："+responseStatus);
//            if (apiDataBean.getStatus()!= 0) {
//                Assert.assertEquals(responseStatus, apiDataBean.getStatus(),
//                        "返回状态码与预期不符合!");
//            }
//            else {
//                // 非2开头状态码为认为是异常请求，抛出异常
//                if (200 > responseStatus || responseStatus >= 300) {
//                    reportUtil.log("返回状态码非200开头："+EntityUtils.toString(response.getEntity(), "UTF-8"));
//                    throw new Exception("返回状态码异常："+ responseStatus);
//                }
//            }
//            HttpEntity respEntity = response.getEntity();
//            Header respContentType = response.getFirstHeader("Content-Type");
//            if (respContentType != null && respContentType.getValue() != null
//                    &&  (respContentType.getValue().contains("download") || respContentType.getValue().contains("octet-stream"))) {
//                String conDisposition = response.getFirstHeader(
//                        "Content-disposition").getValue();
//                String fileType = conDisposition.substring(
//                        conDisposition.lastIndexOf("."),
//                        conDisposition.length());
//                String filePath = "download/" + randomUtil.getRandom(8, false)
//                        + fileType;
//                InputStream is = response.getEntity().getContent();
//                Assert.assertTrue(fileUtil.writeFile(is, filePath), "下载文件失败。");
//                // 将下载文件的路径放到{"filePath":"xxxxx"}进行返回
//                responseData = "{\"filePath\":\"" + filePath + "\"}";
//            } else {
//                responseData=EntityUtils.toString(respEntity, "UTF-8");
//            }
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            method.abort();
//        }
//        // 输出返回数据log
//        reportUtil.log("resp:" + responseData);
//        // 验证预期信息
//        verifyResult(responseData, apiDataBean.getVerify(),
//                apiDataBean.isContains());
//
//        // 对返回结果进行提取保存。
//        saveResult(responseData, apiDataBean.getSave());
//    }
//
//    private String buildRequestParam(ApiDataBean apiDataBean) {
//        // 分析处理预参数 （函数生成的参数）
//        String preParam = buildParam(apiDataBean.getPreParam());
//        savePreParam(preParam);// 保存预存参数 用于后面接口参数中使用和接口返回验证中
//        // 处理参数
//        String apiParam = buildParam(apiDataBean.getParam());
////        System.out.println(apiParam);
//        return apiParam;
//    }
//    /*
//     * 获取Excel文件中设置的关键性Header信息，例如：Content-Type，Authorization等
//     */
//    private String buildRequestHeader(ApiDataBean apiDataBean) {
//        String header = "";
//        header = buildParam(apiDataBean.getHeader());
//        return header;
//    }
//    /*
//     * 这里的header是Excel中设置的json字符串
//     */
//    private HttpUriRequest parseHttpRequest(String url, String method,String header,String param) throws ParseException, IOException {
//        Map<String,String> publicMaps = new HashMap<String,String>();
//        for(Header he : publicHeaders) {
//            publicMaps.put(he.getName(), he.getValue());
//        }
//        // 处理url
//        url = parseUrl(url);
//        reportUtil.log("method:" + method);
//        reportUtil.log("url:" + url);
//        reportUtil.log("publicHeaders:" + JSONObject.toJSONString(publicMaps));
//        reportUtil.log("header:" + header);
//        reportUtil.log("param:" + param.replace("\r\n", "").replace("\n", ""));
//        if(header != null) {
//            @SuppressWarnings("unchecked")
//            Map<String,String> headers = JSON.parseObject(header,HashMap.class);
//            publicMaps.putAll(headers);
//        }
//        //使用Content-Type的值来判定具体body上传模式
//        List<String> values = new ArrayList<String>();
//        for(String s : publicMaps.keySet()) {
//            values.add(publicMaps.get(s));
//        }
//        System.out.println(values);
//        //upload表示上传，也是使用post进行请求
//        if ("post".equalsIgnoreCase(method) || "upload".equalsIgnoreCase(method)) {
//            // 封装post方法
//            HttpPost postMethod = new HttpPost(url);
//            Set<Map.Entry<String, String>> set = publicMaps.entrySet();
//            Iterator<Map.Entry<String, String>> it = set.iterator();
//            while(it.hasNext()) {
//                Map.Entry<String, String> entry = it.next();
//                //如果遇到"Content-Type:multipart/form-data"的情况，请不要加入该请求头。
//                //通过抓包可以发现，
//                //一般Content-Type:multipart/form-data 后面会加上一串 boundary=--------------------------016172816456888939258535的信息
//                //这个信息是动态变化的。
//                if(entry.getValue().equals("multipart/form-data")) {
//                    continue;
//                }else {
//                    postMethod.addHeader(entry.getKey(),entry.getValue());
//                }
//            }
//            //根据请求头的content-type的值，来分别选择上传形式
//            HttpEntity entity  = parseEntity(param,values);
//            postMethod.setEntity(entity);
//            return postMethod;
//        } else if ("put".equalsIgnoreCase(method)) {
//            // 封装put方法
//            HttpPut putMethod = new HttpPut(url);
//            Set<Map.Entry<String, String>> set = publicMaps.entrySet();
//            Iterator<Map.Entry<String, String>> it = set.iterator();
//            while(it.hasNext()) {
//                Map.Entry<String, String> entry = it.next();
//                putMethod.addHeader(entry.getKey(),entry.getValue());
//            }
//            HttpEntity entity  = parseEntity(param,values);
//            putMethod.setEntity(entity);
//            return putMethod;
//        } else if ("delete".equalsIgnoreCase(method)) {
//            // 封装delete方法
//            HttpDelete deleteMethod = new HttpDelete(url);
//            deleteMethod.setHeaders(publicHeaders);
//            return deleteMethod;
//        }else {
//            // 封装get方法
//            HttpGet getMethod = new HttpGet(url);
//            Set<Map.Entry<String, String>> set = publicMaps.entrySet();
//            Iterator<Map.Entry<String, String>> it = set.iterator();
//            while(it.hasNext()) {
//                Map.Entry<String, String> entry = it.next();
//                getMethod.addHeader(entry.getKey(),entry.getValue());
//            }
//            return getMethod;
//        }
//    }
//
//    /**
//     * 格式化url,替换路径参数等。
//     *
//     * @param shortUrl
//     * @return
//     */
//    private String parseUrl(String shortUrl) {
//        // 替换url中的参数
//        shortUrl = getCommonParam(shortUrl);
//        if (shortUrl.startsWith("http")) {
//            return shortUrl;
//        }
//        if (rooUrlEndWithSlash == shortUrl.startsWith("/")) {
//            if (rooUrlEndWithSlash) {
//                shortUrl = shortUrl.replaceFirst("/", "");
//            } else {
//                shortUrl = "/" + shortUrl;
//            }
//        }
//        return rootUrl + shortUrl;
//    }
//
//    /**
//     * 格式化参数，根据请求头的形式来决定如何封装entity，这里主要列了三种形式。
//     * @param param 参数
//     * @param headerValueList 请求头列表里的数据。根据请求头的数据来决定封装形式。
//     * @return
//     * @throws IOException
//     * @throws ParseException
//     */
//    @SuppressWarnings("unchecked")
//    private HttpEntity parseEntity(String param,List<String> headerValueList) throws ParseException, IOException{
//        int requestBodyNum = 0;
//        for(String headerValue : headerValueList) {
//            if(headerValue.contains("multipart/form-data")) {
//                requestBodyNum = 1;
//            }else if(headerValue.contains("application/x-www-form-urlencoded")) {
//                requestBodyNum = 2;
//            }else if(headerValue.equalsIgnoreCase("application/json")) {
//                requestBodyNum = 3;
//            }
//        }
//        switch (requestBodyNum) {
//            case 1:
//                Map<String, String> paramMap = JSON.parseObject(param,HashMap.class);
//                Charset charset = Charset.defaultCharset();
//                MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
//                        .setCharset(charset);
//                for (String key : paramMap.keySet()) {
//                    String value = paramMap.get(key);
//                    Matcher m = funPattern.matcher(value);
//                    if (m.matches() && m.group(1).equals("bodyfile")) {
//                        value = m.group(2);
//                        builder.addPart(key, new FileBody(new File(value)));
//                    } else {
//                        StringBody stringBody = new StringBody(null == value ? "" : value.toString()
//                                , ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset)); //编码
//                        builder.addPart(key, stringBody);
//                    }
//                }
//                httpEntity = builder.build();
//                break;
//            case 2:
//                Map<String, String> bodyMaps = JSON.parseObject(param,HashMap.class);
//                List<NameValuePair> bodyParams = new ArrayList<NameValuePair>();
//                for(String key: bodyMaps.keySet()) {
//                    String value = bodyMaps.get(key);
//                    bodyParams.add(new BasicNameValuePair(key,value));
//                }
//                httpEntity = new UrlEncodedFormEntity(bodyParams,"UTF-8");
//                break;
//            case 3 :
//                httpEntity = new StringEntity(param,"UTF-8");
//                break;
//        }
//        return httpEntity;
//    }
//}