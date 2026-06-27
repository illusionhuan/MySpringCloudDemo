package com.example.common.exception;

/**
 * 资源未找到异常
 *
 * @author demo
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, String field, Object value) {
        super("RESOURCE_NOT_FOUND",
                String.format("%s not found with %s: %s", resource, field, value));
    }
}
