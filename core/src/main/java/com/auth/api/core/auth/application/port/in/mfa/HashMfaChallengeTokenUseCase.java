package com.auth.api.core.auth.application.port.in.mfa;

public interface HashMfaChallengeTokenUseCase {
    String execute(String value);
}
