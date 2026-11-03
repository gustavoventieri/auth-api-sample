package com.auth.api.core.user.application.port.out;

import com.auth.api.core.user.domain.User;

import java.util.Optional;

public interface UserAuthenticationPersistencePort {
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(String id);

}
