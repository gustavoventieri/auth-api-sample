package com.auth.api.core.auth.application.port.in.mfa;

import com.auth.api.core.auth.application.dto.response.AuthResponse;

public interface VerifyRecoveryCodeUseCase {
    AuthResponse execute(String mfaToken, String recoveryCode, String ip, String device);
}
