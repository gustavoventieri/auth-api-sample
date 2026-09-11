package com.auth.api.framework.auth.config.bean;

import com.auth.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.core.auth.application.port.in.password.ComparePasswordUseCase;
import com.auth.core.auth.application.port.in.refresh.GenerateRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.sign.SignInUseCase;
import com.auth.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.core.auth.application.service.SignInService;
import com.auth.core.shared.email.EmailPort;
import com.auth.core.shared.email.EmailTemplateUseCase;
import com.auth.core.user.application.port.out.UserAuthenticationPersistencePort;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignInBeanConfig {

    @Bean
    public SignInUseCase signInUseCaseBean(
            UserAuthenticationPersistencePort userAuthenticationPort,
            ComparePasswordUseCase comparePasswordUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase,
            GenerateRefreshTokenUseCase generateRefreshTokenUseCase,
            GenerateAccessTokenUseCase generateAccessTokenUseCase,
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        return new SignInService(
                userAuthenticationPort,
                comparePasswordUseCase,
                hashRefreshTokenUseCase,
                generateRefreshTokenUseCase,
                generateAccessTokenUseCase,
                refreshTokenRepositoryPort,
                emailPort,
                emailTemplateUseCase
        );
    }
}