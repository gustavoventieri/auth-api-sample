package com.auth.api.core.auth.application.port.in.sign;

public interface RevokeRefreshTokenUseCase {
    void execute(String sessionId, String userId);
}

