package com.example.user.controller;

import com.example.common.dto.ApiResponse;
import com.example.user.dto.LoginRequest;
import com.example.user.dto.LoginResponse;
import com.example.user.dto.RegisterRequest;
import com.example.user.entity.User;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器（注册、登录）
 *
 * @author demo
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册成功的用户
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<User> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（含 JWT Token）
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }
}
