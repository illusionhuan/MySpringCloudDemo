package com.example.demo.exception;

/**
 * 资源未找到异常
 *
 * @author demo
 */
public class ResourceNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super("RESOURCE_NOT_FOUND",
                String.format("%s not found with %s: %s", resource, field, value));
    }
}
