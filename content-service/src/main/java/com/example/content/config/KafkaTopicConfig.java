package com.example.content.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Topic 配置
 *
 * @author demo
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * 确保 comment-events topic 在启动时自动创建
     *
     * @return topic 配置
     */
    @Bean
    public NewTopic commentEventsTopic() {
        return TopicBuilder.name("comment-events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * 确保 Kafka 内部 __consumer_offsets topic 存在
     * KRaft 模式下不会自动创建，缺失会导致 group coordinator 不可用
     *
     * @return topic 配置
     */
    @Bean
    public NewTopic consumerOffsetsTopic() {
        return TopicBuilder.name("__consumer_offsets")
                .partitions(50)
                .replicas(1)
                .config("cleanup.policy", "compact")
                .config("segment.bytes", "104857600")
                .build();
    }
}
