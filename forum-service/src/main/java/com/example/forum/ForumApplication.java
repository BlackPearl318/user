package com.example.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@EnableFeignClients(basePackages = {"com.example.sms.feign"})
//@Import(CatchExceptions.class) //将异常处理类导入启动类
public class ForumApplication {

    public static void main(String[] args) {

        SpringApplication.run(ForumApplication.class,args);

    }
}
