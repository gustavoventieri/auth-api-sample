package com.auth.api.core.auth.application.port.out;


import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;

import java.util.Optional;

public interface ResetPasswordVerificationTokenPersistencePort {
    void save(ResetPasswordVerificationTokenDomain token);
    Optional<ResetPasswordVerificationTokenDomain> findByUserId(String userId);
    void deleteByUserId(String userId);
}
