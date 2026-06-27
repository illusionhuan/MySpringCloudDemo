package com.example.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.auth.JwtUtil;
import com.example.common.exception.BusinessException;
import com.example.common.exception.ResourceNotFoundException;
import com.example.user.dto.LoginRequest;
import com.example.user.dto.LoginResponse;
import com.example.user.dto.RegisterRequest;
import com.example.user.entity.User;
import com.example.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户业务逻辑层
 *
 * @author demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 创建的用户
     */
    @Transactional
    public User register(RegisterRequest request) {
        log.info("用户注册: {}", request.getUsername());

        // 检查用户名是否已存在
        User existing = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (existing != null) {
            throw new BusinessException("USERNAME_EXISTS", "用户名已存在: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword()) // TODO: BCrypt 加密
                .name(request.getUsername())
                .email(request.getEmail())
                .build();
        userMapper.insert(user);
        log.info("用户注册成功, id: {}", user.getId());
        return user;
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（含 JWT Token）
     */
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS", "用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    public List<User> findAll() {
        log.debug("查询所有用户");
        return userMapper.selectList(null);
    }

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    public User findById(Long id) {
        log.debug("根据 ID 查询用户: {}", id);
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        return user;
    }

    /**
     * 更新用户
     *
     * @param id      用户 ID
     * @param updated 更新信息
     * @return 更新后的用户
     */
    @Transactional
    public User updateUser(Long id, User updated) {
        log.info("更新用户: {}", id);
        User user = findById(id);
        user.setName(updated.getName());
        user.setEmail(updated.getEmail());
        user.setPhone(updated.getPhone());
        userMapper.updateById(user);
        return user;
    }

    /**
     * 删除用户
     *
     * @param id 用户 ID
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("删除用户: {}", id);
        findById(id);
        userMapper.deleteById(id);
    }
}
