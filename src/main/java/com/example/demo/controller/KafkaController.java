package com.example.demo.controller;

import com.example.demo.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Kafka消息控制器
 *
 * @author demo
 */
@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProducerService kafkaProducerService;

    /**
     * 发送消息到Kafka
     *
     * @param key     消息键
     * @param message 消息内容
     * @return 发送结果
     */
    @PostMapping("/send")
    public Map<String, Object> send(@RequestParam String key, @RequestParam String message) {
        kafkaProducerService.sendMessage(key, message);
        return Map.of("status", "sent", "key", key, "message", message);
    }
}

