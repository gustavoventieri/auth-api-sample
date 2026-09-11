package com.auth.core.auth.application.service;

import com.auth.core.auth.application.port.in.refresh.CompareRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.sign.SignOutUseCase;
import com.auth.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.core.auth.domain.RefreshToken;
import com.auth.core.shared.exception.BusinessException;

public class SignOutService implements SignOutUseCase {

    private final RefreshTokenPersistencePort refreshTokenRepositoryPort;
    private final CompareRefreshTokenUseCase compareRefreshTokenUseCase;
    private final HashRefreshTokenUseCase hashRefreshTokenUseCase;

    public SignOutService(
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            CompareRefreshTokenUseCase compareRefreshTokenUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase
    ) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.compareRefreshTokenUseCase = compareRefreshTokenUseCase;
        this.hashRefreshTokenUseCase = hashRefreshTokenUseCase;
    }

    @Override
    public void execute(String refreshToken) {

        RefreshToken storedToken =
                refreshTokenRepositoryPort
                        .findRefreshTokenByHashRefreshToken(hashRefreshTokenUseCase.execute(refreshToken))
                        .orElseThrow(() ->
                                new BusinessException("Invalid refresh token")
                        );

        if (storedToken.isRevoked()) {
            return;
        }

        if (!compareRefreshTokenUseCase.execute(
                refreshToken,
                storedToken.tokenHash()
        )) {
            throw new BusinessException("Invalid refresh token");
        }

        refreshTokenRepositoryPort.save(
                storedToken.revoke()
        );
    }
}