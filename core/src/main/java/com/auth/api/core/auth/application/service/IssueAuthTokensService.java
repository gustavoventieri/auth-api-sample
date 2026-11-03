package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.dto.response.AuthResponse;
import com.auth.api.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.GenerateRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.HashRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.sign.IssueAuthTokensUseCase;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.domain.RefreshTokenDomain;
import com.auth.api.core.user.domain.UserDomain;

public class IssueAuthTokensService implements IssueAuthTokensUseCase {

    private final GenerateAccessTokenUseCase generateAccessTokenUseCase;
    private final GenerateRefreshTokenUseCase generateRefreshTokenUseCase;
    private final HashRefreshTokenUseCase hashRefreshTokenUseCase;
    private final RefreshTokenPersistencePort refreshTokenPersistencePort;

    public IssueAuthTokensService(
            GenerateAccessTokenUseCase generateAccessTokenUseCase,
            GenerateRefreshTokenUseCase generateRefreshTokenUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase,
            RefreshTokenPersistencePort refreshTokenPersistencePort
    ) {
        this.generateAccessTokenUseCase = generateAccessTokenUseCase;
        this.generateRefreshTokenUseCase = generateRefreshTokenUseCase;
        this.hashRefreshTokenUseCase = hashRefreshTokenUseCase;
        this.refreshTokenPersistencePort = refreshTokenPersistencePort;
    }

    @Override
    public AuthResponse execute(UserDomain user, String ip, String device) {
        String refreshToken = generateRefreshTokenUseCase.execute();

        refreshTokenPersistencePort.save(
                RefreshTokenDomain.create(
                        user.id(),
                        hashRefreshTokenUseCase.execute(refreshToken),
                        ip,
                        device
                )
        );

        String accessToken = generateAccessTokenUseCase.execute(user.id(), user.role());

        return new AuthResponse(accessToken, refreshToken);
    }
}
