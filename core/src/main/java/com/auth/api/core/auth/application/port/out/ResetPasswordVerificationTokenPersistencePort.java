package com.auth.api.core.auth.application.port.out;


import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;

import java.util.Optional;

public interface ResetPasswordVerificationTokenPersistencePort {
    void save(ResetPasswordVerificationTokenDomain refreshToken);
    Optional<ResetPasswordVerificationTokenDomain> findResetPasswordVerificationTokenByCode(String token, String userId);
    void deleteByUserId(String userId);
}
