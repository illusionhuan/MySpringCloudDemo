package com.example.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 通知实体
 *
 * @author demo
 */
@TableName("notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收通知的用户 ID */
    private Long userId;

    /** 通知类型 */
    private String type;

    /** 通知内容 */
    private String message;

    /** 是否已读 */
    @Builder.Default
    private boolean read = false;

    private LocalDateTime createdAt;
}
