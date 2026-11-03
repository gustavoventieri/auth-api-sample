package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.HashRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.RenewRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.application.service.RenewRefreshTokenService;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefreshTokenBeanConfig {

    @Bean
    public RenewRefreshTokenUseCase refreshTokenUseCaseConfigBean(
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            GenerateAccessTokenUseCase generateAccessTokenUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase
    ){
        return new RenewRefreshTokenService(
                refreshTokenRepositoryPort,
                userAuthenticationPersistencePort,
                generateAccessTokenUseCase,
                hashRefreshTokenUseCase
        );
    }
}
