package com.auth.api.framework.auth.utils.token;

import com.auth.api.core.auth.application.port.in.emailVerification.GenerateEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.GenerateMfaEmailCodeUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.GenerateResetPasswordVerificationTokenUseCase;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpCodeGenerator implements
        GenerateEmailVerificationTokenUseCase,
        GenerateResetPasswordVerificationTokenUseCase,
        GenerateMfaEmailCodeUseCase {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String execute() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}
