package com.auth.api.framework.user.adapter.port.out;


import com.auth.api.core.user.domain.UserDomain;
import com.auth.api.framework.user.adapter.cast.UserCast;
import com.auth.api.framework.user.adapter.port.out.persistence.SpringUserDataRepository;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.application.port.out.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort, UserAuthenticationPersistencePort {

    private final SpringUserDataRepository springUserDataRepository;

    public UserRepositoryAdapter(SpringUserDataRepository springUserDataRepository){
        this.springUserDataRepository = springUserDataRepository;
    }

    @Override
    public Optional<UserDomain> findByEmail(String email) {
        return springUserDataRepository.findByEmail(email).map(UserCast::toDomain);
    }

    @Override
    public UserDomain save(UserDomain userDomain) {
        return UserCast.toDomain(
                springUserDataRepository.save(UserCast.toEntity(userDomain))
        );
    }

    @Override
    public Optional<UserDomain> findById(String id) {
        return springUserDataRepository.findById(id).map(UserCast::toDomain);
    }
}
