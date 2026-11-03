package com.auth.api.core.auth.application.port.in.sign;

import com.auth.api.core.auth.application.dto.response.AuthResponse;

public interface VerifyEmailVerificationTokenUseCase {
    AuthResponse execute(
            String codeOtp,
            String email,
            String ip,
            String device
    );
}
