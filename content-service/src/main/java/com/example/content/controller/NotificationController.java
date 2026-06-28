package com.example.content.controller;

import com.example.common.dto.ApiResponse;
import com.example.content.entity.Notification;
import com.example.content.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知管理控制器
 *
 * @author demo
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 查询用户的通知
     *
     * @param userId 用户 ID
     * @return 通知列表
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Notification>> getByUserId(@PathVariable Long userId) {
        return ApiResponse.success(notificationService.findByUserId(userId));
    }

    /**
     * 标记通知为已读
     *
     * @param id 通知 ID
     * @return 空响应
     */
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success(null);
    }
}
