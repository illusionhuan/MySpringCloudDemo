package com.example.content.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 评论事件生产者
 *
 * @author demo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "comment-events";

    /**
     * 发送评论新增事件
     *
     * @param commentId 评论 ID
     * @param contentId 帖子 ID
     * @param userId    评论者 ID
     * @param authorId  帖子作者 ID
     */
    public void sendCommentAddedEvent(Long commentId, Long contentId, Long userId, Long authorId) {
        String message = String.format("{\"eventType\":\"COMMENT_ADDED\",\"commentId\":%d,\"contentId\":%d,\"userId\":%d,\"authorId\":%d}",
                commentId, contentId, userId, authorId);
        log.info("发送评论事件: {}", message);
        kafkaTemplate.send(TOPIC, String.valueOf(contentId), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka 发送失败", ex);
                    }
                });
    }
}
