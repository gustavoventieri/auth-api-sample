package com.auth.core.auth.application.port.out;

import com.auth.core.auth.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenPersistencePort {

    void save(RefreshToken refreshToken);
    Optional<RefreshToken> findRefreshTokenByHashRefreshToken(String refreshToken);
    Optional<RefreshToken> findByPublicIdAndUserId(
            String publicId,
            String userId
    );
}
