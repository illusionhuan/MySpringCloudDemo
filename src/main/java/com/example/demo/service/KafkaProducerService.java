package com.example.demo.service;

import com.example.demo.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka消息生产者服务
 *
 * @author demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息到Kafka
     *
     * @param key     消息键
     * @param message 消息内容
     * @return 发送结果Future
     */
    public CompletableFuture<SendResult<String, String>> sendMessage(String key, String message) {
        log.info("Sending message to topic={}, key={}, value={}", KafkaConfig.TOPIC_DEMO, key, message);
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(KafkaConfig.TOPIC_DEMO, key, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully: offset={}", result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message", ex);
            }
        });
        return future;
    }
}
