package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.UserTotpPersistencePort;
import com.auth.api.core.auth.domain.UserTotpDomain;
import com.auth.api.framework.auth.adapter.cast.UserTotpCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringUserTotpDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserTotpAdapter implements UserTotpPersistencePort {

    private final SpringUserTotpDataRepository repository;

    public UserTotpAdapter(SpringUserTotpDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(UserTotpDomain userTotp) {
        repository.save(UserTotpCast.toEntity(userTotp));
    }

    @Override
    public Optional<UserTotpDomain> findByUserId(String userId) {
        return repository.findByUserId(userId).map(UserTotpCast::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
        // Garante que o DELETE vá para o banco antes do INSERT do novo segredo (user_id é UNIQUE).
        repository.flush();
    }
}
