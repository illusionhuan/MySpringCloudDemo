package com.example.demo.consumer;

import com.example.demo.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka消息消费者服务
 *
 * @author demo
 */
@Slf4j
@Service
public class KafkaConsumerService {

    /**
     * 消费demo-topic的消息
     *
     * @param message 消息内容
     */
    @KafkaListener(topics = KafkaConfig.TOPIC_DEMO, groupId = "demo-group")
    public void onMessage(String message) {
        log.info("Received message: {}", message);
        // TODO: 添加业务处理逻辑
    }
}
