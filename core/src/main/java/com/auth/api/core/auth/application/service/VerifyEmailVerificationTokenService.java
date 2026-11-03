package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.dto.response.AuthResponse;
import com.auth.api.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.api.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.GenerateRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.refreshToken.HashRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.in.emailVerification.VerifyEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.domain.EmailVerificationTokenDomain;
import com.auth.api.core.auth.domain.RefreshTokenDomain;
import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

public class VerifyEmailVerificationTokenService implements VerifyEmailVerificationTokenUseCase {

    private final UserAuthenticationPersistencePort userAuthenticationPort;
    private final EmailVerificationTokenPort emailVerificationTokenPort;
    private final GenerateAccessTokenUseCase generateAccessTokenUseCase;
    private final GenerateRefreshTokenUseCase generateRefreshTokenUseCase;
    private final HashRefreshTokenUseCase hashRefreshTokenUseCase;
    private final RefreshTokenPersistencePort refreshTokenRepositoryPort;
    private final HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase;
    private final TransactionManager transactionManager;
    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;



    public VerifyEmailVerificationTokenService (
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
        this.userAuthenticationPort = userAuthenticationPort;
        this.emailVerificationTokenPort = emailVerificationTokenPort;
        this.generateAccessTokenUseCase = generateAccessTokenUseCase;
        this.generateRefreshTokenUseCase = generateRefreshTokenUseCase;
        this.hashRefreshTokenUseCase = hashRefreshTokenUseCase;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.hashEmailVerificationTokenUseCase = hashEmailVerificationTokenUseCase;
        this.transactionManager = transactionManager;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }


    @Override
    public AuthResponse execute(
            String otpCode,
            String email,
            String ip,
            String device
    ) {

        UserDomain userDomain = userAuthenticationPort.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException("Invalid verification code")
                );

        EmailVerificationTokenDomain token =
                emailVerificationTokenPort.findByUserId(userDomain.id())
                        .orElseThrow(() ->
                                new BusinessException("Invalid verification code")
                        );

        if (token.isUsed()) {
            throw new BusinessException("Verification code already used");
        }

        if (token.isExpired()) {
            throw new BusinessException("Verification code expired");
        }

        String otpHash =
                hashEmailVerificationTokenUseCase.execute(otpCode);

        if (!otpHash.equals(token.otpCode())) {
            throw new BusinessException("Invalid verification code");
        }

        UserDomain verifiedUserDomain = userDomain.verify();

        EmailVerificationTokenDomain usedToken =
                token.markAsUsed();

        String refreshToken =
                generateRefreshTokenUseCase.execute();

        String refreshTokenHash =
                hashRefreshTokenUseCase.execute(refreshToken);

        RefreshTokenDomain newRefreshToken =
                RefreshTokenDomain.create(
                        verifiedUserDomain.id(),
                        refreshTokenHash,
                        ip,
                        device
                );

        transactionManager.execute(() -> {

            userAuthenticationPort.save(verifiedUserDomain);

            emailVerificationTokenPort.save(usedToken);

            refreshTokenRepositoryPort.save(newRefreshToken);

            return null;
        });

        String accessToken =
                generateAccessTokenUseCase.execute(
                        verifiedUserDomain.id(),
                        verifiedUserDomain.role()
                );

        emailPort.send(
                new EmailMessage(
                        verifiedUserDomain.email(),
                        "Your account was created",
                        emailTemplateUseCase.buildAccountCreatedEmail(
                                verifiedUserDomain.name(),
                                otpCode
                        )
                )
        );

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }
}
