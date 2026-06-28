package com.example.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 帖子实体
 *
 * @author demo
 */
@TableName("contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 作者 ID */
    private Long userId;

    /** 标题 */
    private String title;

    /** 正文 */
    private String body;

    /** 状态：DRAFT/PUBLISHED/DELETED */
    @Builder.Default
    private String status = "PUBLISHED";

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
