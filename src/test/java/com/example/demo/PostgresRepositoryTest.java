package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostgresRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("应能保存和查询用户")
    void shouldSaveAndRetrieveUser() {
        User user = User.builder()
                .name("Alice")
                .email("alice@example.com")
                .phone("1234567890")
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("Alice", saved.getName());
        assertEquals("alice@example.com", saved.getEmail());
    }

    @Test
    @DisplayName("应能根据邮箱查询用户")
    void shouldFindUserByEmail() {
        User user = User.builder()
                .name("Bob")
                .email("bob@example.com")
                .phone("0987654321")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("bob@example.com");

        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getName());
    }

    @Test
    @DisplayName("查询不存在的邮箱应返回空")
    void shouldReturnEmptyForNonExistentEmail() {
        Optional<User> found = userRepository.findByEmail("nobody@example.com");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("应能查询所有用户")
    void shouldFindAllUsers() {
        userRepository.save(User.builder().name("User1").email("u1@example.com").build());
        userRepository.save(User.builder().name("User2").email("u2@example.com").build());

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("应能根据 ID 删除用户")
    void shouldDeleteUser() {
        User user = userRepository.save(
                User.builder().name("ToDelete").email("del@example.com").build());

        userRepository.deleteById(user.getId());

        assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    @DisplayName("应能更新用户信息")
    void shouldUpdateUser() {
        User user = userRepository.save(
                User.builder().name("OldName").email("update@example.com").build());

        user.setName("NewName");
        User updated = userRepository.save(user);

        assertEquals("NewName", updated.getName());
        assertEquals(user.getId(), updated.getId());
    }
}
