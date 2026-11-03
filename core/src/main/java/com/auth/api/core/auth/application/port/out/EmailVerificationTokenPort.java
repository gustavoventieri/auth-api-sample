package com.auth.api.core.auth.application.port.out;


import com.auth.api.core.auth.domain.EmailVerificationTokenDomain;

import java.util.Optional;

public interface EmailVerificationTokenPort {
    void save(EmailVerificationTokenDomain emailVerificationToken);
    Optional<EmailVerificationTokenDomain> findByUserId(String email);
    void deleteByUserId(String userId);
}
