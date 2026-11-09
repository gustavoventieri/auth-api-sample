package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.RecoveryCodePersistencePort;
import com.auth.api.core.auth.domain.RecoveryCodeDomain;
import com.auth.api.framework.auth.adapter.cast.RecoveryCodeCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringRecoveryCodeDataRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecoveryCodeAdapter implements RecoveryCodePersistencePort {

    private final SpringRecoveryCodeDataRepository repository;

    public RecoveryCodeAdapter(SpringRecoveryCodeDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveAll(List<RecoveryCodeDomain> recoveryCodes) {
        repository.saveAll(recoveryCodes.stream().map(RecoveryCodeCast::toEntity).toList());
    }

    @Override
    public void save(RecoveryCodeDomain recoveryCode) {
        repository.save(RecoveryCodeCast.toEntity(recoveryCode));
    }

    @Override
    public List<RecoveryCodeDomain> findUnusedByUserId(String userId) {
        return repository.findByUserIdAndUsedAtIsNull(userId).stream()
                .map(RecoveryCodeCast::toDomain)
                .toList();
    }

    @Override
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }
}
