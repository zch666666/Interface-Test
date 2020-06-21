package com.interfacetest.entity;

import lombok.Data;

@Data
public class apiDataBean {
    private boolean run; //是否运行
    private String desc; // 接口描述
    private String method;//访问方法
    private String url; //接口访问url

    //header的情况相对复杂，可以设置公共header，也可以针对个别请求来设定访问的header。
    private String header;
    private String body;//接口请求时的body
    private boolean contains; //是否对返回的json字符串中作包含判定（针对接口返回内容较多，不好具体制定路径的情况）
    private int status; //返回状态
    private String verify;//判定内容
    private String save; //需要保存的内容
    private String param; //接口发送需要的参数
    private int sleep; //暂停时间
}
