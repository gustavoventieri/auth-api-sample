package com.auth.core.auth.application.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record AuthResponse(
    String accessToken,
    String refreshToken
) {
}
