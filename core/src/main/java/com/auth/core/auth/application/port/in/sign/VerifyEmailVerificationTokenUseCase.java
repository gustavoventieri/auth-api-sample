package com.auth.core.auth.application.port.in.sign;

import com.auth.core.auth.application.dto.response.AuthResponse;

public interface VerifyEmailVerificationTokenUseCase {
    AuthResponse execute(
            String codeOtp,
            String email,
            String ip,
            String device
    );
}
