package com.auth.core.auth.application.port.in.accessToken;

public interface ExtractUserIdFromAccessTokenUseCase {
    String execute(String token);
}

