package com.auth.api.core.auth.application.port.in.mfa;

import com.auth.api.core.auth.application.dto.response.MfaSetupCompletedResponse;

public interface ConfirmTotpSetupUseCase {
    MfaSetupCompletedResponse execute(String mfaToken, String code, String ip, String device);
}
