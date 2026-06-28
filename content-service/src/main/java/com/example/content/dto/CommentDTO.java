package com.example.content.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论传输对象
 *
 * @author demo
 */
@Data
public class CommentDTO {
    private Long id;
    private Long contentId;
    private Long userId;
    private String userName;
    private String body;
    private LocalDateTime createdAt;
}
