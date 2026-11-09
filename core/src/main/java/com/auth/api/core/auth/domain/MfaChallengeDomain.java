package com.auth.api.core.auth.domain;

import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Desafio MFA criado após a senha ser validada. O token bruto vai para o cliente
 * e apenas o hash fica persistido. É de uso único, expira rápido e aceita
 * um número limitado de códigos errados.
 */
@Builder(toBuilder = true)
public record MfaChallengeDomain(
        String id,
        String userId,
        String tokenHash,
        MfaChallengePurpose purpose,
        int failedAttempts,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime usedAt
) implements ExpirableToken {

    public static final Duration TTL = Duration.ofMinutes(5);
    public static final int MAX_FAILED_ATTEMPTS = 5;

    public static MfaChallengeDomain create(String userId, String tokenHash, MfaChallengePurpose purpose) {
        LocalDateTime now = LocalDateTime.now();

        return MfaChallengeDomain.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .tokenHash(tokenHash)
                .purpose(purpose)
                .failedAttempts(0)
                .createdAt(now)
                .expiresAt(now.plus(TTL))
                .build();
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean hasAttemptsLeft() {
        return failedAttempts < MAX_FAILED_ATTEMPTS;
    }

    public boolean isUsable() {
        return !isUsed() && !isExpired() && hasAttemptsLeft();
    }

    public MfaChallengeDomain registerFailedAttempt() {
        return this.toBuilder()
                .failedAttempts(failedAttempts + 1)
                .build();
    }

    public MfaChallengeDomain markAsUsed() {
        return this.toBuilder()
                .usedAt(LocalDateTime.now())
                .build();
    }
}
