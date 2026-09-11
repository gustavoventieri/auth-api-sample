package com.auth.api.framework.auth.utils.token;

import com.auth.core.auth.application.port.in.emailVerification.GenerateEmailVerificationTokenUseCase;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class EmailVerificationTokenGenerator
        implements GenerateEmailVerificationTokenUseCase {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public EmailVerificationTokenGenerator(){

    }

    @Override
    public String execute() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}