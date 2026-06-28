package com.example.content;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * 内容服务启动类
 *
 * @author demo
 */
@SpringBootApplication(scanBasePackages = {"com.example.content", "com.example.common"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableKafka
@MapperScan("com.example.content.repository")
public class ContentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentServiceApplication.class, args);
    }
}
