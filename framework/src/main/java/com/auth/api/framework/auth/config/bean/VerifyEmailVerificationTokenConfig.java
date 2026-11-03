package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.api.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.refresh.GenerateRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.sign.VerifyEmailVerificationTokenUseCase;

import com.auth.api.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.application.service.VerifyEmailVerificationTokenService;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VerifyEmailVerificationTokenConfig {

    @Bean
    public VerifyEmailVerificationTokenUseCase verifyEmailVerificationTokenUseCase(
            UserAuthenticationPersistencePort userAuthenticationPort,
            EmailVerificationTokenPort emailVerificationTokenPort,
            GenerateAccessTokenUseCase generateAccessTokenUseCase,
            GenerateRefreshTokenUseCase generateRefreshTokenUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase,
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ){
        return new VerifyEmailVerificationTokenService(
            userAuthenticationPort,
            emailVerificationTokenPort,
            generateAccessTokenUseCase,
            generateRefreshTokenUseCase,
            hashRefreshTokenUseCase,
            refreshTokenRepositoryPort,
            hashEmailVerificationTokenUseCase,
            transactionManager,
            emailPort,
            emailTemplateUseCase
        );
    }
}
