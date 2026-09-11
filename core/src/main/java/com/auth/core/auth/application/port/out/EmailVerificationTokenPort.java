package com.auth.core.auth.application.port.out;

import com.auth.core.auth.domain.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenPort {
    void save(EmailVerificationToken emailVerificationToken);
    Optional<EmailVerificationToken> findByUserId(String email);
    void deleteByUserId(String userId);
}
