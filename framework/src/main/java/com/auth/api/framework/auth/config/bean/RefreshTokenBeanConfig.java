package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.api.core.auth.application.port.in.refresh.CompareRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.sign.RefreshTokenUseCase;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.application.service.RefreshTokenService;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefreshTokenBeanConfig {

    @Bean
    public RefreshTokenUseCase refreshTokenUseCaseConfigBean(
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            CompareRefreshTokenUseCase compareRefreshTokenUseCase,
            GenerateAccessTokenUseCase generateAccessTokenUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase
    ){
        return new RefreshTokenService(
                refreshTokenRepositoryPort,
                userAuthenticationPersistencePort,
                compareRefreshTokenUseCase,
                generateAccessTokenUseCase,
                hashRefreshTokenUseCase
        );
    }
}
