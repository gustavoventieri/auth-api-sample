package com.auth.api.core.auth.application.port.in.emailVerification;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;

public interface VerifyEmailVerificationTokenUseCase {
    MfaChallengeResponse execute(
            String codeOtp,
            String email
    );
}
