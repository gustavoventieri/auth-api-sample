package com.auth.api.framework.auth.config.bean;

import com.auth.core.auth.application.port.in.refresh.CompareRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.sign.SignOutUseCase;
import com.auth.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.core.auth.application.service.SignOutService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignOutConfig {


    @Bean
    public SignOutUseCase signOutUseCaseConfig(
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            CompareRefreshTokenUseCase compareRefreshTokenUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase
    ){
        return new SignOutService(
                refreshTokenRepositoryPort,
                compareRefreshTokenUseCase,
                hashRefreshTokenUseCase
        );
    }

}
