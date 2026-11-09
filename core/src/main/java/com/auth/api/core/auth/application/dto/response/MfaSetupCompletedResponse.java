package com.auth.api.core.auth.application.dto.response;

import java.util.List;

public record MfaSetupCompletedResponse(
        AuthResponse authResponse,
        List<String> recoveryCodes
) {
}
