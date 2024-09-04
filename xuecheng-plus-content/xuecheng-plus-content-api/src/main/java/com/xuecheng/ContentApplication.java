package com.xuecheng;


import com.spring4all.swagger.EnableSwagger2Doc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;



@SpringBootApplication
@EnableSwagger2Doc //生成接口文档
@Slf4j
@EnableFeignClients(basePackages={"com.xuecheng.content.feignclient"})
public class ContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class,args);
        log.info("服务启动成功...");
    }
}
