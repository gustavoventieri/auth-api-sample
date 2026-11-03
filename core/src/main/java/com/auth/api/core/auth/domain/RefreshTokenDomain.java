package com.auth.api.core.auth.domain;

import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record RefreshTokenDomain(
        Long id,
        String userId,
        String tokenHash,
        String ip,
        String device,
        String publicId,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        LocalDateTime revokedAt
) implements ExpirableToken {

    private static final Duration TTL = Duration.ofDays(30);

    public static RefreshTokenDomain create(
            String userId,
            String tokenHash,
            String ip,
            String device
    ) {
        LocalDateTime now = LocalDateTime.now();

        return RefreshTokenDomain.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .ip(ip)
                .device(device)
                .publicId(UUID.randomUUID().toString())
                .createdAt(now)
                .expiresAt(now.plus(TTL))
                .build();
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public RefreshTokenDomain revoke() {
        return this.toBuilder()
                .revokedAt(LocalDateTime.now())
                .build();
    }
}
