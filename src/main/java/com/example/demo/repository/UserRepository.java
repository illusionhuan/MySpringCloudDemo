package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户数据访问层
 *
 * @author demo
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据邮箱查询用户
     *
     * @param email 用户邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);
}
