package com.auth.api.core.user.application.port.out;

import com.auth.api.core.user.domain.UserDomain;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<UserDomain> findByEmail(String email);
    UserDomain save(UserDomain userDomain);
    Optional<UserDomain> findById(String id);
}
