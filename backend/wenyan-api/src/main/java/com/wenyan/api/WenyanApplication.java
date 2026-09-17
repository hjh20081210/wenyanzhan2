package com.wenyan.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/** 文言斩启动类 */
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.wenyan")
public class WenyanApplication {
    public static void main(String[] args) {
        SpringApplication.run(WenyanApplication.class, args);
        System.out.println("== 文言斩后端启动成功 ==");
    }
}
