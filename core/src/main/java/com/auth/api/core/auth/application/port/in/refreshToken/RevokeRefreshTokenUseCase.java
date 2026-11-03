package com.auth.api.core.auth.application.port.in.refreshToken;

public interface RevokeRefreshTokenUseCase {
    void execute(String sessionId, String userId);
}

