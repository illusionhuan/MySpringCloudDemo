package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
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

    private final UserRepository userRepository;

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建后的用户
     */
    @Transactional
    public User createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    public List<User> findAll() {
        log.debug("Finding all users");
        return userRepository.findAll();
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    public User findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 用户邮箱
     * @return 用户信息
     * @throws ResourceNotFoundException 用户不存在时抛出
     */
    public User findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * 更新用户信息
     *
     * @param id      用户ID
     * @param updated 更新的用户信息
     * @return 更新后的用户
     */
    @Transactional
    public User updateUser(Long id, User updated) {
        log.info("Updating user with id: {}", id);
        User user = findById(id);
        user.setName(updated.getName());
        user.setEmail(updated.getEmail());
        user.setPhone(updated.getPhone());
        User savedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", id);
        return savedUser;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        // 先检查用户是否存在
        findById(id);
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }
}
