package com.interfacetest.mapper;

import com.interfacetest.entity.ApiData;
import java.util.List;

public interface ApiDataMapper {

     List<ApiData> selectAllApiData();
     int insertApiData(ApiData role);
}
