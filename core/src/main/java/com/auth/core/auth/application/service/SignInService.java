package com.auth.core.auth.application.service;


import com.auth.core.auth.application.dto.response.AuthResponse;
import com.auth.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.core.auth.application.port.in.password.ComparePasswordUseCase;
import com.auth.core.auth.application.port.in.refresh.GenerateRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.sign.SignInUseCase;
import com.auth.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.core.auth.domain.RefreshToken;
import com.auth.core.shared.email.EmailMessage;
import com.auth.core.shared.email.EmailPort;
import com.auth.core.shared.email.EmailTemplateUseCase;
import com.auth.core.shared.exception.BusinessException;
import com.auth.core.shared.exception.NotFoundException;
import com.auth.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.core.user.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class SignInService implements SignInUseCase {

    private final UserAuthenticationPersistencePort userAuthenticationPort;
    private final ComparePasswordUseCase comparePasswordUseCase;
    private final GenerateAccessTokenUseCase generateAccessTokenUseCase;
    private final GenerateRefreshTokenUseCase generateRefreshTokenUseCase;
    private final HashRefreshTokenUseCase hashRefreshTokenUseCase;
    private final RefreshTokenPersistencePort refreshTokenRepositoryPort;

    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;


    public SignInService(
            UserAuthenticationPersistencePort userAuthenticationPort,
            ComparePasswordUseCase comparePasswordUseCase,
            HashRefreshTokenUseCase hashRefreshTokenUseCase,
            GenerateRefreshTokenUseCase generateRefreshTokenUseCase,
            GenerateAccessTokenUseCase generateAccessTokenUseCase,
            RefreshTokenPersistencePort refreshTokenRepositoryPort,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
        ){
        this.userAuthenticationPort = userAuthenticationPort;
        this.comparePasswordUseCase = comparePasswordUseCase;
        this.hashRefreshTokenUseCase = hashRefreshTokenUseCase;
        this.generateRefreshTokenUseCase = generateRefreshTokenUseCase;
        this.generateAccessTokenUseCase =  generateAccessTokenUseCase;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    @Override
    public AuthResponse execute(String email, String password, String ip, String device) {

        User user = userAuthenticationPort.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email or Password Invalid"));


        if (!user.isVerified()) throw new BusinessException("Email not verified yet");

        if (!user.isActive()) throw new BusinessException("User not Active");

        boolean passwordMatches = comparePasswordUseCase.execute(
                password,
                user.password()
        );

        if (!passwordMatches) throw new NotFoundException("Email or Password Invalid");

        String accessToken = generateAccessTokenUseCase.execute(
                user.id(),
                user.role()
        );

        String refreshToken = generateRefreshTokenUseCase.execute();

        String refreshTokenHash = hashRefreshTokenUseCase.execute(refreshToken);

        RefreshToken entity = RefreshToken.create(
                user.id(),
                refreshTokenHash,
                ip,
                device
        );

        refreshTokenRepositoryPort.save(entity);

        emailPort.send(
                new EmailMessage(
                        user.email(),
                        "New sign-in to your account",
                        emailTemplateUseCase.buildNewLoginEmail(
                                user.name(),
                                device,
                                ip,
                                LocalDateTime.now().format(
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")
                                )
                        )
                )
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
