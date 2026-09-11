package com.auth.api.framework.auth.utils.token;

import com.auth.core.auth.application.port.in.refresh.GenerateRefreshTokenUseCase;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenGenerator implements GenerateRefreshTokenUseCase {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final int TOKEN_SIZE = 32;

    @Override
    public String execute() {

        byte[] bytes = new byte[TOKEN_SIZE];

        SECURE_RANDOM.nextBytes(bytes);

        return ENCODER.encodeToString(bytes);
    }
}