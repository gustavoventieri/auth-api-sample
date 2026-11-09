package com.auth.api.core.auth.application.port.out;

import com.auth.api.core.auth.domain.UserTotpDomain;

import java.util.Optional;

public interface UserTotpPersistencePort {
    void save(UserTotpDomain userTotp);
    Optional<UserTotpDomain> findByUserId(String userId);
    void deleteByUserId(String userId);
}
