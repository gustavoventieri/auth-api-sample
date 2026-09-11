package com.auth.api.framework.auth.utils.token;

import com.auth.core.auth.application.port.in.refresh.CompareRefreshTokenUseCase;
import com.auth.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class RefreshTokenComparator implements CompareRefreshTokenUseCase {

    private final HashRefreshTokenUseCase hashRefreshTokenUseCase;

    public RefreshTokenComparator(
            HashRefreshTokenUseCase hashRefreshTokenUseCase
    ) {
        this.hashRefreshTokenUseCase = hashRefreshTokenUseCase;
    }

    @Override
    public boolean execute(
            String refreshToken,
            String tokenHash
    ) {

        String generatedHash =
                hashRefreshTokenUseCase.execute(refreshToken);

        return MessageDigest.isEqual(
                generatedHash.getBytes(StandardCharsets.UTF_8),
                tokenHash.getBytes(StandardCharsets.UTF_8)
        );
    }

}