package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.UserTotpPersistencePort;
import com.auth.api.core.auth.domain.UserTotpDomain;
import com.auth.api.framework.auth.adapter.cast.UserTotpCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringUserTotpDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.SpringUserDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserTotpAdapter implements UserTotpPersistencePort {

    private final SpringUserDataRepository userRepository;

    private final SpringUserTotpDataRepository repository;

    public UserTotpAdapter(
            SpringUserTotpDataRepository repository,
            SpringUserDataRepository userRepository
    ) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @Override
    public void save(UserTotpDomain userTotp) {
        repository.save(UserTotpCast.toEntity(userTotp, userRef(userTotp.userId())));
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

    /** Proxy gerenciado do usuário para a FK (não faz SELECT e respeita o @Version do UserEntity). */
    private UserEntity userRef(String userId) {
        return userRepository.getReferenceById(userId);
    }
}
