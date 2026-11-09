package com.auth.api.core.auth.application.port.in.mfa;

import com.auth.api.core.auth.application.dto.response.TotpSetupResponse;

public interface SetupTotpUseCase {
    TotpSetupResponse execute(String mfaToken);
}
