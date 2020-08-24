package com.interfacetest;
import com.interfacetest.dataprovider.GetSwagger;
import com.interfacetest.dataprovider.WriteTestCaseToDB;

public class ApiTestApplication {
    public static void main(String[] args) {
        //Capture interface information and store it as Json file
        GetSwagger.run();
        //Extract interface information, automatically generate test parameters and store to database
        WriteTestCaseToDB.run();
    }
}