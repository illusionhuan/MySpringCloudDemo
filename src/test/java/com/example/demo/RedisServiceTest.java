package com.example.demo;

import com.example.demo.service.RedisService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @AfterEach
    void cleanup() {
        redisService.delete("test:key");
        redisService.delete("test:ttl");
        redisService.delete("test:obj");
    }

    @Test
    @DisplayName("应能设置和获取字符串值")
    void shouldSetAndGetString() {
        redisService.set("test:key", "hello");

        Object result = redisService.get("test:key");

        assertEquals("hello", result);
    }

    @Test
    @DisplayName("应能设置带过期时间的值")
    void shouldSetWithTTL() {
        redisService.set("test:ttl", "expires", 60, TimeUnit.SECONDS);

        Boolean exists = redisService.hasKey("test:ttl");

        assertTrue(exists);
        assertEquals("expires", redisService.get("test:ttl"));
    }

    @Test
    @DisplayName("应能删除键")
    void shouldDeleteKey() {
        redisService.set("test:key", "to-delete");

        Boolean deleted = redisService.delete("test:key");

        assertTrue(deleted);
        assertFalse(redisService.hasKey("test:key"));
    }

    @Test
    @DisplayName("应能检查键是否存在")
    void shouldCheckExistence() {
        assertFalse(redisService.hasKey("test:key"));

        redisService.set("test:key", "exists");

        assertTrue(redisService.hasKey("test:key"));
    }

    @Test
    @DisplayName("查询不存在的键应返回空")
    void shouldReturnNullForMissingKey() {
        Object result = redisService.get("non:existent");
        assertNull(result);
    }

    @Test
    @DisplayName("应能设置和获取对象值")
    void shouldSetAndGetObject() {
        var data = java.util.Map.of("name", "Alice", "age", 30);

        redisService.set("test:obj", data);
        Object result = redisService.get("test:obj");

        assertNotNull(result);
    }
}
