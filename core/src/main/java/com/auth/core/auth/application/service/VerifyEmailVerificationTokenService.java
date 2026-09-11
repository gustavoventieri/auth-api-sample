package com.auth.core.auth.application.service;

import com.auth.core.auth.application.dto.response.AuthResponse;
import com.auth.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.GenerateRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.sign.VerifyEmailVerificationTokenUseCase;
import com.auth.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.core.auth.domain.EmailVerificationToken;
import com.auth.core.auth.domain.RefreshToken;
import com.auth.core.shared.email.EmailMessage;
import com.auth.core.shared.email.EmailPort;
import com.auth.core.shared.email.EmailTemplateUseCase;
import com.auth.core.shared.exception.BusinessException;
import com.auth.core.shared.transaction.TransactionManager;
import com.auth.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.core.user.domain.User;

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

        User user = userAuthenticationPort.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException("Invalid verification code")
                );

        EmailVerificationToken token =
                emailVerificationTokenPort.findByUserId(user.id())
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

        User verifiedUser = user.verify();

        EmailVerificationToken usedToken =
                token.markAsUsed();

        String refreshToken =
                generateRefreshTokenUseCase.execute();

        String refreshTokenHash =
                hashRefreshTokenUseCase.execute(refreshToken);

        RefreshToken newRefreshToken =
                RefreshToken.create(
                        verifiedUser.id(),
                        refreshTokenHash,
                        ip,
                        device
                );

        transactionManager.execute(() -> {

            userAuthenticationPort.save(verifiedUser);

            emailVerificationTokenPort.save(usedToken);

            refreshTokenRepositoryPort.save(newRefreshToken);

            return null;
        });

        String accessToken =
                generateAccessTokenUseCase.execute(
                        verifiedUser.id(),
                        verifiedUser.role()
                );

        emailPort.send(
                new EmailMessage(
                        verifiedUser.email(),
                        "Your account was created",
                        emailTemplateUseCase.buildAccountCreatedEmail(
                                verifiedUser.name(),
                                otpCode
                        )
                )
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
