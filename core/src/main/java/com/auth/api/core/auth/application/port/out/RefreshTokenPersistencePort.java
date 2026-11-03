package com.auth.api.core.auth.application.port.out;


import com.auth.api.core.auth.domain.RefreshTokenDomain;

import java.util.Optional;

public interface RefreshTokenPersistencePort {

    void save(RefreshTokenDomain refreshToken);
    Optional<RefreshTokenDomain> findRefreshTokenByHashRefreshToken(String refreshToken);
    Optional<RefreshTokenDomain> findByPublicIdAndUserId(
            String publicId,
            String userId
    );
}
