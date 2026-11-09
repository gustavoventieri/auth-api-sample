package com.auth.api.core.auth.application.port.out;


import com.auth.api.core.auth.domain.RefreshTokenDomain;

import java.util.Optional;

public interface RefreshTokenPersistencePort {

    void save(RefreshTokenDomain refreshToken);
    Optional<RefreshTokenDomain> findRefreshTokenByHashRefreshToken(String refreshToken);
    /** Revoga todas as sessões ativas do usuário e retorna quantas foram revogadas. */
    int revokeAllActiveByUserId(String userId);

    Optional<RefreshTokenDomain> findByPublicIdAndUserId(
            String publicId,
            String userId
    );
}
