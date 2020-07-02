package com.interfacetest.dataprovider;

import com.interfacetest.util.DBUtil;
import org.apache.ibatis.session.SqlSession;

public class TestData {
    private SqlSession sqlSession= DBUtil.getSession();


}
