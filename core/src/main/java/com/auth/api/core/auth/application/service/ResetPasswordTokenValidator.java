package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.port.in.password.reset.HashResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.out.ResetPasswordVerificationTokenPersistencePort;
import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.exception.NotFoundException;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

import static com.auth.api.core.shared.util.SecurityUtils.constantTimeEquals;
import static com.auth.api.core.shared.util.ValidationUtils.requireNotBlank;

public class ResetPasswordTokenValidator {

    public record ValidResetPasswordToken(
            UserDomain user,
            ResetPasswordVerificationTokenDomain token
    ) {}

    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;
    private final ResetPasswordVerificationTokenPersistencePort tokenPersistencePort;
    private final HashResetPasswordVerificationTokenUseCase hashTokenUseCase;

    public ResetPasswordTokenValidator(
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            ResetPasswordVerificationTokenPersistencePort tokenPersistencePort,
            HashResetPasswordVerificationTokenUseCase hashTokenUseCase
    ) {
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
        this.tokenPersistencePort = tokenPersistencePort;
        this.hashTokenUseCase = hashTokenUseCase;
    }

    public ValidResetPasswordToken validate(String email, String otpCode) {
        requireNotBlank(email, "Email is empty");
        requireNotBlank(otpCode, "Token is empty");

        UserDomain user = userAuthenticationPersistencePort.findByEmail(email)
                .filter(UserDomain::isActiveAndVerified)
                .orElseThrow(() -> new NotFoundException("Something went wrong"));

        ResetPasswordVerificationTokenDomain token = tokenPersistencePort.findByUserId(user.id())
                .orElseThrow(() -> new BusinessException("Token is invalid"));

        if (!token.isUsable()) {
            throw new BusinessException("Token is expired or was used");
        }

        if (!constantTimeEquals(hashTokenUseCase.execute(otpCode), token.otpCode())) {
            throw new BusinessException("Token is invalid");
        }

        return new ValidResetPasswordToken(user, token);
    }
}
