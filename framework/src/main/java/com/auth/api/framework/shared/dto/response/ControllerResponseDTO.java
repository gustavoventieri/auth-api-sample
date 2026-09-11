package com.auth.api.framework.shared.dto.response;

public record ControllerResponseDTO<T>(
        String message,
        T data
) {
}