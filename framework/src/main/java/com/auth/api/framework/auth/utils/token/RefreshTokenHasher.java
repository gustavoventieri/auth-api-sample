package com.auth.api.framework.auth.utils.token;

import com.auth.core.auth.application.port.in.refresh.HashRefreshTokenUseCase;
import com.auth.api.framework.auth.utils.hash.Hasher256Service;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenHasher implements HashRefreshTokenUseCase {

    private final Hasher256Service sha256Hasher;

    public RefreshTokenHasher(Hasher256Service sha256Hasher) {
        this.sha256Hasher = sha256Hasher;
    }

    @Override
    public String execute(String refreshToken) {
        return sha256Hasher.hash(refreshToken);
    }
}