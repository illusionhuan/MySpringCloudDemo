package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 用户实体类
 *
 * @author demo
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** 用户ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 用户邮箱 */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /** 用户手机号 */
    @Column(length = 20)
    private String phone;
}
