package com.interfacetest.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ApiData implements ApiDataInterface{
    private Integer id;
    private String url; //接口访问url
    private String method;//访问方法
    private String parameters; //接口发送需要的参数
    private Integer statusCode;
    private String summary; // 接口描述
    private String produces;
    private String operationId;
    private String consumes;
    private String tags;
    private Timestamp createTime;
    private boolean isRun;
}
