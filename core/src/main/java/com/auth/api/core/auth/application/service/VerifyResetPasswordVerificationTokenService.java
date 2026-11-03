package com.auth.api.core.auth.application.service;

import com.auth.api.core.auth.application.port.in.password.reset.GenerateResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.HashResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.VerifyResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.out.ResetPasswordVerificationTokenPersistencePort;

public class VerifyResetPasswordVerificationTokenService implements VerifyResetPasswordVerificationTokenUseCase {

    private final ResetPasswordVerificationTokenPersistencePort resetPasswordVerificationTokenPersistencePort;
    private final HashResetPasswordVerificationTokenUseCase hashResetPasswordVerificationTokenUseCase;


    public VerifyResetPasswordVerificationTokenService(
            ResetPasswordVerificationTokenPersistencePort resetPasswordVerificationTokenPersistencePort,
            HashResetPasswordVerificationTokenUseCase hashResetPasswordVerificationTokenUseCase
    ){
        this.resetPasswordVerificationTokenPersistencePort = resetPasswordVerificationTokenPersistencePort;
        this.hashResetPasswordVerificationTokenUseCase = hashResetPasswordVerificationTokenUseCase;
    }


    @Override
    public boolean execute(String token, String userId) {
        return false;
    }
}
