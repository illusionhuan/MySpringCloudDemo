package com.example.user.dto;

import lombok.Data;

/**
 * 登录请求
 *
 * @author demo
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
