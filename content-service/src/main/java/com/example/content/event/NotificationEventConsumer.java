package com.example.content.event;

import com.example.content.entity.Notification;
import com.example.content.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 通知事件消费者
 *
 * @author demo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 消费评论事件
     */
    @KafkaListener(topics = "comment-events", groupId = "notification-group")
    public void handleCommentEvent(String message) {
        log.info("收到评论事件: {}", message);

        try {
            JsonNode node = objectMapper.readTree(message);
            long contentId = node.get("contentId").asLong();
            long authorId = node.get("authorId").asLong();

            Notification notification = Notification.builder()
                    .userId(authorId)
                    .type("COMMENT_ADDED")
                    .message("您的帖子(ID:" + contentId + ") 收到了新评论")
                    .build();
            notificationService.createNotification(notification);
            log.info("通知创建成功, authorId: {}, contentId: {}", authorId, contentId);
        } catch (Exception e) {
            log.error("处理评论事件失败", e);
        }
    }
}
