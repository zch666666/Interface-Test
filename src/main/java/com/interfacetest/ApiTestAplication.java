package com.interfacetest;
import com.interfacetest.util.SwaggerToJson;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiTestAplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ApiTestAplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        init();
    }

    /**
     * 初始化信息
     */
    @SuppressWarnings("unused")
    private void init() throws InterruptedException {
        //SwaggerToJson.run();
        Thread.sleep(10000);
        System.out.println("GetSwaggerJsonSuccessfully");
    }

}