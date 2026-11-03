package com.auth.api.framework.auth.utils.token;

import com.auth.api.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.HashResetPasswordVerificationTokenUseCase;
import com.auth.api.framework.auth.utils.hash.Hasher256Service;
import org.springframework.stereotype.Component;

@Component
public class OtpCodeHasher implements
        HashEmailVerificationTokenUseCase,
        HashResetPasswordVerificationTokenUseCase {

    private final Hasher256Service sha256Hasher;

    public OtpCodeHasher(Hasher256Service sha256Hasher) {
        this.sha256Hasher = sha256Hasher;
    }

    @Override
    public String execute(String otpCode) {
        return sha256Hasher.hash(otpCode);
    }
}
