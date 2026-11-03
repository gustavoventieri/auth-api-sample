package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.port.in.password.reset.VerifyResetPasswordVerificationTokenUseCase;

public class VerifyResetPasswordVerificationTokenService implements VerifyResetPasswordVerificationTokenUseCase {

    private final ResetPasswordTokenValidator resetPasswordTokenValidator;

    public VerifyResetPasswordVerificationTokenService(ResetPasswordTokenValidator resetPasswordTokenValidator) {
        this.resetPasswordTokenValidator = resetPasswordTokenValidator;
    }

    @Override
    public boolean execute(String token, String email) {
        resetPasswordTokenValidator.validate(email, token);
        return true;
    }
}
