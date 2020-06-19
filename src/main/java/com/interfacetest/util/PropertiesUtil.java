package com.interfacetest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class PropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static final String DEFAULT_PROPERTIES="/projectConfig.properties";
    private static Properties props;

    static{
        loadProps();
    }

    synchronized static private void loadProps(){
        props = new Properties();
        InputStream in = null;
        try {
            in = PropertiesUtil.class.getResourceAsStream(DEFAULT_PROPERTIES);
//            System.out.println(PropertiesUtil.class.getResource("").getPath());
//            System.out.println(PropertiesUtil.class.getResource("/").getPath());
//            System.out.println(PropertiesUtil.class.getClassLoader().getResource("").getPath());
            props.load(in);
        } catch (FileNotFoundException e) {
            logger.error("properties文件未找到");
        } catch (IOException e) {
            logger.error("PropertiesUtil:IOException");
        } finally {
            try {
                if(null != in) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("PropertiesUtil:properties文件流关闭出现异常");
            }
        }
        logger.info("加载properties文件内容完成...........");
        //logger.info("properties文件内容：" + props);
    }

    public static String getProperty(String key){
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if(null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }
}
