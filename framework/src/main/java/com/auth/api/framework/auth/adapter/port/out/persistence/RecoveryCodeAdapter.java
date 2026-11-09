package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.RecoveryCodePersistencePort;
import com.auth.api.core.auth.domain.RecoveryCodeDomain;
import com.auth.api.framework.auth.adapter.cast.RecoveryCodeCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringRecoveryCodeDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.SpringUserDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecoveryCodeAdapter implements RecoveryCodePersistencePort {

    private final SpringUserDataRepository userRepository;

    private final SpringRecoveryCodeDataRepository repository;

    public RecoveryCodeAdapter(
            SpringRecoveryCodeDataRepository repository,
            SpringUserDataRepository userRepository
    ) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @Override
    public void saveAll(List<RecoveryCodeDomain> recoveryCodes) {
        repository.saveAll(recoveryCodes.stream().map(rc -> RecoveryCodeCast.toEntity(rc, userRef(rc.userId()))).toList());
    }

    @Override
    public void save(RecoveryCodeDomain recoveryCode) {
        repository.save(RecoveryCodeCast.toEntity(recoveryCode, userRef(recoveryCode.userId())));
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

    /** Proxy gerenciado do usuário para a FK (não faz SELECT e respeita o @Version do UserEntity). */
    private UserEntity userRef(String userId) {
        return userRepository.getReferenceById(userId);
    }
}
