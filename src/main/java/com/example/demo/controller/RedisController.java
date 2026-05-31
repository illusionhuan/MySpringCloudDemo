package com.example.demo.controller;

import com.example.demo.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存操作控制器
 *
 * @author demo
 */
@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    @PostMapping("/set")
    public void set(@RequestParam String key, @RequestParam String value) {
        redisService.set(key, value);
    }

    /**
     * 设置缓存并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间（秒）
     */
    @PostMapping("/set-with-ttl")
    public void setWithTtl(@RequestParam String key, @RequestParam String value,
                           @RequestParam long timeout) {
        redisService.set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 键值对
     */
    @GetMapping("/get")
    public Map<String, Object> get(@RequestParam String key) {
        return Map.of("key", key, "value", redisService.get(key));
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam String key) {
        return Map.of("key", key, "deleted", redisService.delete(key));
    }

    /**
     * 检查键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    @GetMapping("/exists")
    public Map<String, Object> exists(@RequestParam String key) {
        return Map.of("key", key, "exists", redisService.hasKey(key));
    }
}
