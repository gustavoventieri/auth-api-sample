package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.port.in.sign.RevokeRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.domain.RefreshTokenDomain;
import com.auth.api.core.shared.exception.BusinessException;

public class RevokeRefreshTokenService implements RevokeRefreshTokenUseCase {

    private final RefreshTokenPersistencePort refreshTokenRepositoryPort;

    public RevokeRefreshTokenService(
            RefreshTokenPersistencePort refreshTokenRepositoryPort
    ) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
    }

    @Override
    public void execute(String sessionId, String userId) {

        RefreshTokenDomain refreshToken =
                refreshTokenRepositoryPort
                        .findByPublicIdAndUserId(sessionId, userId)
                        .orElseThrow(() ->
                                new BusinessException("Session not found")
                        );

        if (refreshToken.isRevoked()) {
            throw new BusinessException("Session already revoked");
        }

        refreshTokenRepositoryPort.save(
                refreshToken.revoke()
        );
    }
}