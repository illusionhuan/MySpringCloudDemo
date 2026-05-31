package com.example.demo;

import com.example.demo.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KafkaProducerConsumerTest {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        // 生产者配置
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
        producer = new KafkaProducer<>(producerProps);

        // 消费者配置
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + System.currentTimeMillis());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(consumerProps);
    }

    @AfterEach
    void tearDown() {
        if (producer != null) producer.close();
        if (consumer != null) consumer.close();
    }

    @Test
    @DisplayName("应能正常发送和消费消息")
    void shouldProduceAndConsumeMessage() throws Exception {
        String topic = KafkaConfig.TOPIC_DEMO;
        String key = "test-key";
        String value = "test-message-" + System.currentTimeMillis();

        // 发送消息
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        Future<RecordMetadata> future = producer.send(record);
        future.get(); // 等待发送完成

        // 消费消息
        consumer.subscribe(Collections.singletonList(topic));
        var records = consumer.poll(Duration.ofSeconds(10));

        assertFalse(records.isEmpty(), "应至少接收到一条消息");

        ConsumerRecord<String, String> received = records.iterator().next();
        assertEquals(key, received.key());
        assertEquals(value, received.value());
    }

    @Test
    @DisplayName("应能发送多条消息")
    void shouldProduceMultipleMessages() throws Exception {
        String topic = KafkaConfig.TOPIC_DEMO;
        int messageCount = 5;

        for (int i = 0; i < messageCount; i++) {
            producer.send(new ProducerRecord<>(topic, "key-" + i, "msg-" + i)).get();
        }

        consumer.subscribe(Collections.singletonList(topic));
        var records = consumer.poll(Duration.ofSeconds(10));

        assertTrue(records.count() >= messageCount,
                "应至少接收到 " + messageCount + " 条消息");
    }

    @Test
    @DisplayName("应能处理空 key 的消息")
    void shouldHandleNullKeyMessage() throws Exception {
        String topic = KafkaConfig.TOPIC_DEMO;
        String value = "no-key-message";

        producer.send(new ProducerRecord<>(topic, null, value)).get();

        consumer.subscribe(Collections.singletonList(topic));
        var records = consumer.poll(Duration.ofSeconds(10));

        assertFalse(records.isEmpty());
        boolean found = false;
        for (ConsumerRecord<String, String> r : records) {
            if (value.equals(r.value())) {
                found = true;
                assertNull(r.key());
                break;
            }
        }
        assertTrue(found, "应能找到 key 为空的消息");
    }
}
