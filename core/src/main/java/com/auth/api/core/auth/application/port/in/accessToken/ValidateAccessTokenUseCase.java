package com.auth.api.core.auth.application.port.in.accessToken;

public interface ValidateAccessTokenUseCase {
    boolean execute(String token);
}
