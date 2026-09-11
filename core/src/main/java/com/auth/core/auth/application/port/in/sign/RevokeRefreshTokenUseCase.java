package com.auth.core.auth.application.port.in.sign;

public interface RevokeRefreshTokenUseCase {
    void execute(String sessionId, String userId);
}

