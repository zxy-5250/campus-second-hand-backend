package com.secondhand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.secondhand.mapper")
public class SecondHandApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecondHandApplication.class, args);
        System.out.println("======== 校园二手交易平台后端启动成功，端口 8080 ========");
    }
}
