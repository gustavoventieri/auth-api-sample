package com.auth.api.framework.shared.handler.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record ErrorResponse (
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}