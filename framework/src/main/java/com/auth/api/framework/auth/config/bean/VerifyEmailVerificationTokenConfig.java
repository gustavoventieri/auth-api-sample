package com.auth.api.framework.auth.config.bean;

import com.auth.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.GenerateRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.sign.VerifyEmailVerificationTokenUseCase;

import com.auth.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.core.auth.application.service.VerifyEmailVerificationTokenService;
import com.auth.core.shared.email.EmailPort;
import com.auth.core.shared.email.EmailTemplateUseCase;
import com.auth.core.shared.transaction.TransactionManager;
import com.auth.core.user.application.port.out.UserAuthenticationPersistencePort;
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
