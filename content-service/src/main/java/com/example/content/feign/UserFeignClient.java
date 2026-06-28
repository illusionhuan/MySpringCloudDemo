package com.example.content.feign;

import com.example.common.dto.ApiResponse;
import com.example.content.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * User Service Feign 客户端
 *
 * @author demo
 */
@FeignClient(name = "user-service", fallbackFactory = UserFeignFallbackFactory.class)
public interface UserFeignClient {

    /**
     * 根据 ID 获取用户信息
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @GetMapping("/api/users/{id}")
    ApiResponse<UserDTO> getUserById(@PathVariable("id") Long id);
}
