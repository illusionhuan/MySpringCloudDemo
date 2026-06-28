package com.example.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.exception.ResourceNotFoundException;
import com.example.content.entity.Notification;
import com.example.content.repository.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知业务逻辑层
 *
 * @author demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;

    /**
     * 创建通知
     *
     * @param notification 通知信息
     */
    @Transactional
    public void createNotification(Notification notification) {
        log.info("创建通知, userId: {}, type: {}", notification.getUserId(), notification.getType());
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    /**
     * 查询用户的通知
     *
     * @param userId 用户 ID
     * @return 通知列表
     */
    public List<Notification> findByUserId(Long userId) {
        log.debug("查询用户通知: {}", userId);
        return notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .orderByDesc(Notification::getCreatedAt));
    }

    /**
     * 标记通知为已读
     *
     * @param id 通知 ID
     */
    @Transactional
    public void markAsRead(Long id) {
        log.info("标记通知已读: {}", id);
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new ResourceNotFoundException("Notification", "id", id);
        }
        notification.setRead(true);
        notificationMapper.updateById(notification);
    }
}
