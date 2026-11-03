package com.auth.api.core.auth.application.dto.response;


public record AuthResponse(
    String accessToken,
    String refreshToken
) {
}
