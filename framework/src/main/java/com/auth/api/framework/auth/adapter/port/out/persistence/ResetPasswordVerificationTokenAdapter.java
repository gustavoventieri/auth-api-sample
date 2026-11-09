package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.ResetPasswordVerificationTokenPersistencePort;
import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;
import com.auth.api.framework.auth.adapter.cast.ResetPasswordVerificationTokenCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringResetPasswordVerificationTokenDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.SpringUserDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ResetPasswordVerificationTokenAdapter implements ResetPasswordVerificationTokenPersistencePort {

    private final SpringUserDataRepository userRepository;

    private final SpringResetPasswordVerificationTokenDataRepository repository;

    public ResetPasswordVerificationTokenAdapter(
            SpringResetPasswordVerificationTokenDataRepository repository,
            SpringUserDataRepository userRepository
    ) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @Override
    public void save(ResetPasswordVerificationTokenDomain token) {
        repository.save(ResetPasswordVerificationTokenCast.toEntity(token, userRef(token.userId())));
    }

    @Override
    public Optional<ResetPasswordVerificationTokenDomain> findByUserId(String userId) {
        return repository.findByUserId(userId).map(ResetPasswordVerificationTokenCast::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }

    /** Proxy gerenciado do usuário para a FK (não faz SELECT e respeita o @Version do UserEntity). */
    private UserEntity userRef(String userId) {
        return userRepository.getReferenceById(userId);
    }
}
