package com.auth.api.core.auth.domain;

import java.time.LocalDateTime;

public interface ExpirableToken {

    LocalDateTime expiresAt();

    default boolean isExpired() {
        return expiresAt().isBefore(LocalDateTime.now());
    }
}
