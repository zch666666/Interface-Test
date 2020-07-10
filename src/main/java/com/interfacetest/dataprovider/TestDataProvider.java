package com.interfacetest.dataprovider;

import com.interfacetest.entity.ApiData;
import com.interfacetest.util.DBUtil;
import org.apache.ibatis.session.SqlSession;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//select data from DB and provide data for test
public class TestDataProvider {

    public static List<ApiData> dataList;

    static {
        dataList=readUnRunDataFromDB();
    }

    @DataProvider(name="apiDataList")
    public static Iterator<Object[]> getTestDataList(Method method){
        List<Object[]> dataProvider = new ArrayList<>();
        for (ApiData data : dataList) {
                dataProvider.add(new Object[] { data });
        }
        //System.out.println(dataList.size());
        return dataProvider.iterator();
    }

    private static List<ApiData> readUnRunDataFromDB(){
        List<ApiData> dataList;
        SqlSession sqlSession= DBUtil.getSession();
        dataList=sqlSession.selectList("selectNotRun");
        return dataList;
    }
}
