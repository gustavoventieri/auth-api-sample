package com.auth.api.core.auth.application.service.refreshToken;

import com.auth.api.core.auth.application.dto.response.AuthResponse;
import com.auth.api.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.HashRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.RenewRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.domain.RefreshTokenDomain;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.exception.NotFoundException;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

public class RenewRefreshTokenService implements RenewRefreshTokenUseCase {

    private final RefreshTokenPersistencePort refreshTokenRepositoryPort;
    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;
    private final GenerateAccessTokenUseCase generateAccessTokenUseCase;
    private final HashRefreshTokenUseCase hashRefreshTokenUseCase;

    public RenewRefreshTokenService(
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            GenerateAccessTokenUseCase generateAccessTokenUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase
    ) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
        this.generateAccessTokenUseCase = generateAccessTokenUseCase;
        this.hashRefreshTokenUseCase = hashRefreshTokenUseCase;
    }

    @Override
    public AuthResponse execute(String refreshToken) {

        RefreshTokenDomain storedToken = refreshTokenRepositoryPort
                .findRefreshTokenByHashRefreshToken(hashRefreshTokenUseCase.execute(refreshToken))
                .orElseThrow(() -> new NotFoundException("Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new BusinessException("Refresh token revoked");
        }

        if (storedToken.isExpired()) {
            refreshTokenRepositoryPort.save(storedToken.revoke());
            throw new BusinessException("Refresh token expired");
        }

        UserDomain userDomain = userAuthenticationPersistencePort.findById(storedToken.userId())
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        String accessToken = generateAccessTokenUseCase.execute(userDomain.id(), userDomain.role());

        return new AuthResponse(accessToken, refreshToken);
    }
}
