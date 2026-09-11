package com.auth.core.user.application.port.out;

import com.auth.core.user.domain.User;

import java.util.Optional;

public interface UserAuthenticationPersistencePort {
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(String id);

}
