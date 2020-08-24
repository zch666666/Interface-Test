package com.interfacetest.entity;

import lombok.Data;

@Data
public class ApiParam {
    private String paramName;
    //参数所属服务名称
    private String serviceName;
    private ParamDataType paramDataType;
    private String paramContent;
    private ParameterDescription parameterDescription;
}
