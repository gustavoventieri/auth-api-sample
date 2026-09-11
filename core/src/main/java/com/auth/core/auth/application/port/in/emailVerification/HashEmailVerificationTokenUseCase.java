package com.auth.core.auth.application.port.in.emailVerification;

public interface HashEmailVerificationTokenUseCase {
    String execute(String otpCode);
}
