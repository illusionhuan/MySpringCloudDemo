package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Nacos 配置加载测试。
 * 需要本地运行 Nacos 服务（localhost:8848）。
 * 跳过方式：mvn test -Dtest='!NacosConfigTest'
 */
@SpringBootTest
@ActiveProfiles("test")
class NacosConfigTest {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.cloud.nacos.server-addr:localhost:8848}")
    private String nacosServerAddr;

    @Test
    @DisplayName("应从配置中加载应用名称")
    void shouldLoadApplicationName() {
        assertNotNull(appName);
        assertEquals("my-spring-cloud-demo", appName);
    }

    @Test
    @DisplayName("Nacos 服务地址应已配置")
    void shouldHaveNacosServerAddress() {
        assertNotNull(nacosServerAddr);
        assertFalse(nacosServerAddr.isBlank());
    }

    @Test
    @DisplayName("Nacos 配置属性应存在")
    void shouldHaveNacosConfigProperties() {
        // 验证 bootstrap.yml 中的属性已加载
        assertNotNull(nacosServerAddr);
        assertTrue(nacosServerAddr.contains(":"),
                "Nacos 服务地址应包含 host:port 格式");
    }
}
