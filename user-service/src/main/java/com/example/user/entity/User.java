package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 用户实体
 *
 * @author demo
 */
@TableName("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录用户名 */
    private String username;

    /** 密码（BCrypt 加密） */
    private String password;

    /** 显示名称 */
    private String name;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;
}
