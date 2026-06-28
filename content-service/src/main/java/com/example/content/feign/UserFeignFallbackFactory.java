package com.example.content.feign;

import com.example.common.dto.ApiResponse;
import com.example.content.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * UserFeign 降级工厂
 *
 * @author demo
 */
@Slf4j
@Component
public class UserFeignFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        log.error("Feign 调用 user-service 失败", cause);
        return new UserFeignClient() {
            @Override
            public ApiResponse<UserDTO> getUserById(Long id) {
                UserDTO fallback = new UserDTO();
                fallback.setId(id);
                fallback.setName("未知用户");
                return ApiResponse.success(fallback);
            }
        };
    }
}
