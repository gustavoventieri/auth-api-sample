package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.port.in.password.reset.HashResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.VerifyResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.out.ResetPasswordVerificationTokenPersistencePort;
import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.exception.NotFoundException;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class VerifyResetPasswordVerificationTokenService implements VerifyResetPasswordVerificationTokenUseCase {

    private final ResetPasswordVerificationTokenPersistencePort resetPasswordVerificationTokenPersistencePort;
    private final HashResetPasswordVerificationTokenUseCase hashResetPasswordVerificationTokenUseCase;
    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;

    public VerifyResetPasswordVerificationTokenService(
            ResetPasswordVerificationTokenPersistencePort resetPasswordVerificationTokenPersistencePort,
            HashResetPasswordVerificationTokenUseCase hashResetPasswordVerificationTokenUseCase,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort
    ) {
        this.resetPasswordVerificationTokenPersistencePort = resetPasswordVerificationTokenPersistencePort;
        this.hashResetPasswordVerificationTokenUseCase = hashResetPasswordVerificationTokenUseCase;
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
    }

    @Override
    public boolean execute(String token, String email) {

        if (email == null || email.isBlank()) {
            throw new BusinessException("Email is empty");
        }

        if (token == null || token.isBlank()) {
            throw new BusinessException("Token is empty");
        }

        UserDomain userDomain = userAuthenticationPersistencePort.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Something went wrong"));

        if (!userDomain.isActive() || !userDomain.isVerified()) {
            throw new BusinessException("Something went wrong");
        }

        String hashedToken = hashResetPasswordVerificationTokenUseCase.execute(token);

        ResetPasswordVerificationTokenDomain tokenDomain =
                resetPasswordVerificationTokenPersistencePort
                        .findResetPasswordVerificationTokenByCode(hashedToken, email)
                        .orElseThrow(() -> new BusinessException("Token is invalid"));

        if (tokenDomain.isExpired() || tokenDomain.isUsed()) {
            throw new BusinessException("Token is expired or was used");
        }

        boolean matches = MessageDigest.isEqual(
                hashedToken.getBytes(StandardCharsets.UTF_8),
                tokenDomain.otpCode().getBytes(StandardCharsets.UTF_8)
        );

        if (!matches) {
            throw new BusinessException("Token is invalid");
        }

        return true;
    }
}