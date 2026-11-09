package com.auth.api.core.auth.application.port.out;

import com.auth.api.core.auth.domain.MfaChallengeDomain;

import java.util.Optional;

public interface MfaChallengePersistencePort {
    void save(MfaChallengeDomain challenge);
    Optional<MfaChallengeDomain> findByTokenHash(String tokenHash);
    void deleteByUserId(String userId);
}
