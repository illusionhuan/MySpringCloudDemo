package com.example.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 评论实体
 *
 * @author demo
 */
@TableName("comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 帖子 ID */
    private Long contentId;

    /** 评论者 ID */
    private Long userId;

    /** 评论内容 */
    private String body;

    private LocalDateTime createdAt;
}
