package com.auth.api.core.auth.domain;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record EmailVerificationTokenDomain(
        String id,
        String userId,
        String otpCode,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime usedAt
) implements OneTimeToken {

    public static EmailVerificationTokenDomain create(String userId, String otpHash) {
        LocalDateTime now = LocalDateTime.now();

        return EmailVerificationTokenDomain.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .otpCode(otpHash)
                .createdAt(now)
                .expiresAt(now.plus(DEFAULT_TTL))
                .build();
    }

    public EmailVerificationTokenDomain markAsUsed() {
        return this.toBuilder()
                .usedAt(LocalDateTime.now())
                .build();
    }
}
