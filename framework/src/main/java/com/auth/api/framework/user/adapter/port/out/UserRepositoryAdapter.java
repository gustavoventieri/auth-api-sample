package com.auth.api.framework.user.adapter.port.out;


import com.auth.api.framework.user.adapter.cast.UserCast;
import com.auth.api.framework.user.adapter.port.out.persistence.SpringUserDataRepository;
import com.auth.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.core.user.application.port.out.UserRepositoryPort;
import com.auth.core.user.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort, UserAuthenticationPersistencePort {

    private final SpringUserDataRepository springUserDataRepository;

    public UserRepositoryAdapter(SpringUserDataRepository springUserDataRepository){
        this.springUserDataRepository = springUserDataRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springUserDataRepository.findByEmail(email).map(UserCast::toDomain);
    }

    @Override
    public User save(User user) {
        return UserCast.toDomain(
                springUserDataRepository.save(UserCast.toEntity(user))
        );
    }

    @Override
    public Optional<User> findById(String id) {
        return springUserDataRepository.findById(id).map(UserCast::toDomain);
    }
}
