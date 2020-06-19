package com.interfacetest;
import com.interfacetest.dataprovider.GetSwagger;
import com.interfacetest.dataprovider.WriteDataToDB;

public class ApiTestApplication {
    public static void main(String[] args) {
        GetSwagger.run();
        //WriteDataToDB.run();
    }
}