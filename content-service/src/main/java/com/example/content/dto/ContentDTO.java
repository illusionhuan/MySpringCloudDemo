package com.example.content.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 帖子传输对象
 *
 * @author demo
 */
@Data
public class ContentDTO {
    private Long id;
    private Long userId;
    private String authorName;
    private String title;
    private String body;
    private String status;
    private LocalDateTime createdAt;
}
