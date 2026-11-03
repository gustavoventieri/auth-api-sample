package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.ResetPasswordVerificationTokenPersistencePort;
import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;
import com.auth.api.framework.auth.adapter.cast.ResetPasswordVerificationTokenCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringResetPasswordVerificationTokenDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ResetPasswordVerificationTokenAdapter implements ResetPasswordVerificationTokenPersistencePort {

    private final SpringResetPasswordVerificationTokenDataRepository repository;

    public ResetPasswordVerificationTokenAdapter(SpringResetPasswordVerificationTokenDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(ResetPasswordVerificationTokenDomain token) {
        repository.save(ResetPasswordVerificationTokenCast.toEntity(token));
    }

    @Override
    public Optional<ResetPasswordVerificationTokenDomain> findByUserId(String userId) {
        return repository.findByUserId(userId).map(ResetPasswordVerificationTokenCast::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }
}
