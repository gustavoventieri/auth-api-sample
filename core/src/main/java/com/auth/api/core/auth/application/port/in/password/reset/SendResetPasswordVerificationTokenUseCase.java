package com.auth.api.core.auth.application.port.in.password.reset;

public interface SendResetPasswordVerificationTokenUseCase {
    void execute(String userId);
}
