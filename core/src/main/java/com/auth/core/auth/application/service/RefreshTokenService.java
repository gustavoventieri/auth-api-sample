package com.auth.core.auth.application.service;

import com.auth.core.auth.application.dto.response.AuthResponse;
import com.auth.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.CompareRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.sign.RefreshTokenUseCase;
import com.auth.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.core.auth.domain.RefreshToken;
import com.auth.core.shared.exception.BusinessException;
import com.auth.core.shared.exception.NotFoundException;
import com.auth.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.core.user.domain.User;



public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenPersistencePort refreshTokenRepositoryPort;
    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;
    private final CompareRefreshTokenUseCase compareRefreshTokenUseCase;
    private final GenerateAccessTokenUseCase generateAccessTokenUseCase;
    private final HashRefreshTokenUseCase hashRefreshTokenUseCase;

    public RefreshTokenService(
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            CompareRefreshTokenUseCase compareRefreshTokenUseCase,
            GenerateAccessTokenUseCase generateAccessTokenUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase
    ){
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.compareRefreshTokenUseCase = compareRefreshTokenUseCase;
        this.generateAccessTokenUseCase = generateAccessTokenUseCase;
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
        this.hashRefreshTokenUseCase = hashRefreshTokenUseCase;
    }

    @Override
    public AuthResponse execute(String refreshToken) {

        RefreshToken storedToken = refreshTokenRepositoryPort
                .findRefreshTokenByHashRefreshToken(hashRefreshTokenUseCase.execute(refreshToken))
                .orElseThrow(() ->
                        new NotFoundException("Invalid refresh token")
                );

        if (storedToken.isExpired()) {

            refreshTokenRepositoryPort.save(storedToken.revoke());

            throw new BusinessException("Refresh token expired");
        }

        if (storedToken.isRevoked()) {
            throw new BusinessException("Refresh token revoked");
        }

        if (!compareRefreshTokenUseCase.execute(
                refreshToken,
                storedToken.tokenHash()
        )) {
            throw new BusinessException("Invalid refresh token");
        }

        User user = userAuthenticationPersistencePort.findById(storedToken.userId())
                .orElseThrow(() ->
                        new NotFoundException("User Not Found")
                );;

        String accessToken = generateAccessTokenUseCase.execute(
                storedToken.userId(),
                user.role()
        );

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }


}
