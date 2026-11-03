package com.auth.api.core.auth.application.port.in.password.reset;

public interface VerifyResetPasswordVerificationTokenUseCase {
    boolean execute(String token, String email);
}
