package com.auth.api.framework.auth.utils.token;

import com.auth.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.api.framework.auth.utils.hash.Hasher256Service;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationTokenHash implements HashEmailVerificationTokenUseCase {
    private final Hasher256Service sha256Hasher;

    public EmailVerificationTokenHash(Hasher256Service sha256Hasher) {
        this.sha256Hasher = sha256Hasher;
    }

    @Override
    public String execute(String otpCode) {
        return sha256Hasher.hash(otpCode);
    }
}
