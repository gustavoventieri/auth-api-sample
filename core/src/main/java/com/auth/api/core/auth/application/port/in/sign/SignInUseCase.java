package com.auth.api.core.auth.application.port.in.sign;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;

public interface SignInUseCase {
    MfaChallengeResponse execute(String email, String password);
}
