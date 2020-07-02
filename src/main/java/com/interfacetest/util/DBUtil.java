package com.interfacetest.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class DBUtil {
    private static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<>();
    private static SqlSessionFactory sqlSessionFactory;

    static {
        InputStream is=null;
        // 读取mybatis核心配置文件
        try {
           is = Resources.getResourceAsStream("mybatis-config.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建会话工厂
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
    }

    public static SqlSession getSession() {
        // 获取会话
        SqlSession sqlSession = threadLocal.get();
        if (sqlSession == null) {
            // 从会话工厂获取session
            sqlSession = sqlSessionFactory.openSession();
            // 绑定会话工厂
            threadLocal.set(sqlSession);
        }
        return sqlSession;
    }

    public static void close() {
        SqlSession sqlSession = threadLocal.get();
        if (sqlSession != null) {
            sqlSession.close();
        }
    }
}
