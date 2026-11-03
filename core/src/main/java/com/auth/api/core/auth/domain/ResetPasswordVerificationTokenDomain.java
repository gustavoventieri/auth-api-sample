package com.auth.api.core.auth.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record ResetPasswordVerificationTokenDomain(
        String id,
        String userId,
        String otpCode,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime usedAt
) {
}
