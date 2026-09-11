package com.auth.core.auth.domain;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record EmailVerificationToken(
        String id,
        String userId,
        String otpCode,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime usedAt
) {

    public static EmailVerificationToken create(
            String userId,
            String otpHash
    ) {

        LocalDateTime now = LocalDateTime.now();

        return EmailVerificationToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .otpCode(otpHash)
                .createdAt(now)
                .expiresAt(now.plusMinutes(10))
                .build();
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public EmailVerificationToken markAsUsed() {
        return this.toBuilder()
                .usedAt(LocalDateTime.now())
                .build();
    }

}