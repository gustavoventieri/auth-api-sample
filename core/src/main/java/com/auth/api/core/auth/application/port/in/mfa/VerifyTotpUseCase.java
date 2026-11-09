package com.auth.api.core.auth.application.port.in.mfa;

import com.auth.api.core.auth.application.dto.response.AuthResponse;

public interface VerifyTotpUseCase {
    AuthResponse execute(String mfaToken, String code, String ip, String device);
}
