package com.auth.api.core.auth.application.port.in.refreshToken;

public interface HashRefreshTokenUseCase {
    String execute(String refreshToken);
}
