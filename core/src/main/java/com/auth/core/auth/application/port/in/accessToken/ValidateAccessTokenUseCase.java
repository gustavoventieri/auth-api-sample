package com.auth.core.auth.application.port.in.accessToken;

public interface ValidateAccessTokenUseCase {
    boolean execute(String token);
}
