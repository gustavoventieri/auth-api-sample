package com.auth.api.core.auth.application.port.out;

import com.auth.api.core.auth.domain.RecoveryCodeDomain;

import java.util.List;

public interface RecoveryCodePersistencePort {
    void saveAll(List<RecoveryCodeDomain> recoveryCodes);
    void save(RecoveryCodeDomain recoveryCode);
    List<RecoveryCodeDomain> findUnusedByUserId(String userId);
    void deleteByUserId(String userId);
}
