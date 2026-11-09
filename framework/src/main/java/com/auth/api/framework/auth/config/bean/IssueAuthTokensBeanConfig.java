package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.GenerateRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.HashRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.sign.IssueAuthTokensUseCase;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.application.service.sign.IssueAuthTokensService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IssueAuthTokensBeanConfig {

    @Bean
    public IssueAuthTokensUseCase issueAuthTokensUseCaseBean(
            GenerateAccessTokenUseCase generateAccessTokenUseCase,
            GenerateRefreshTokenUseCase generateRefreshTokenUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase,
            RefreshTokenPersistencePort refreshTokenPersistencePort
    ) {
        return new IssueAuthTokensService(
                generateAccessTokenUseCase,
                generateRefreshTokenUseCase,
                hashRefreshTokenUseCase,
                refreshTokenPersistencePort
        );
    }
}
