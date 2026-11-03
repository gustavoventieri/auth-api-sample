package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.port.in.refreshToken.HashRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.sign.SignOutUseCase;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.domain.RefreshTokenDomain;
import com.auth.api.core.shared.exception.BusinessException;

public class SignOutService implements SignOutUseCase {

    private final RefreshTokenPersistencePort refreshTokenRepositoryPort;
    private final HashRefreshTokenUseCase hashRefreshTokenUseCase;

    public SignOutService(
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            HashRefreshTokenUseCase hashRefreshTokenUseCase
    ) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.hashRefreshTokenUseCase = hashRefreshTokenUseCase;
    }

    @Override
    public void execute(String refreshToken) {

        RefreshTokenDomain storedToken = refreshTokenRepositoryPort
                .findRefreshTokenByHashRefreshToken(hashRefreshTokenUseCase.execute(refreshToken))
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (storedToken.isRevoked()) {
            return;
        }

        refreshTokenRepositoryPort.save(storedToken.revoke());
    }
}
