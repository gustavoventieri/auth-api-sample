package com.auth.core.auth.application.port.in.refresh;

public interface HashRefreshTokenUseCase {
    String execute(String refreshToken);
}
