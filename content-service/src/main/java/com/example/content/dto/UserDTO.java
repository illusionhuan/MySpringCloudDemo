package com.example.content.dto;

import lombok.Data;

/**
 * 用户信息（Feign 调用返回）
 *
 * @author demo
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
}
