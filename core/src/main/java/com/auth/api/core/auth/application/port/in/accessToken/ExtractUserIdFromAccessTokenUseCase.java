package com.auth.api.core.auth.application.port.in.accessToken;

public interface ExtractUserIdFromAccessTokenUseCase {
    String execute(String token);
}

