package com.example.user.dto;

import lombok.Data;

/**
 * 注册请求
 *
 * @author demo
 */
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
}
