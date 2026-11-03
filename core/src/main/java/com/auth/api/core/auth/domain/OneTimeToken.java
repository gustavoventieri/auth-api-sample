package com.auth.api.core.auth.domain;

import java.time.Duration;
import java.time.LocalDateTime;

public interface OneTimeToken extends ExpirableToken {

    Duration DEFAULT_TTL = Duration.ofMinutes(10);

    String otpCode();

    LocalDateTime usedAt();

    default boolean isUsed() {
        return usedAt() != null;
    }

    default boolean isUsable() {
        return !isUsed() && !isExpired();
    }
}
