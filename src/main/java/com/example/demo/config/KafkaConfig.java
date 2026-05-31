package com.example.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka配置类
 *
 * @author demo
 */
@Configuration
public class KafkaConfig {

    /** Demo主题名称 */
    public static final String TOPIC_DEMO = "demo-topic";

    /**
     * 创建Demo主题
     *
     * @return Kafka主题
     */
    @Bean
    public NewTopic demoTopic() {
        return TopicBuilder.name(TOPIC_DEMO)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
