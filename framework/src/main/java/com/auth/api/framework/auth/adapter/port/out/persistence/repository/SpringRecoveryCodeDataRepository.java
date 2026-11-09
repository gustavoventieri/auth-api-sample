package com.auth.api.framework.auth.adapter.port.out.persistence.repository;

import com.auth.api.framework.auth.adapter.port.out.persistence.entity.RecoveryCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringRecoveryCodeDataRepository extends JpaRepository<RecoveryCodeEntity, String> {

    List<RecoveryCodeEntity> findByUserIdAndUsedAtIsNull(String userId);

    void deleteByUserId(String userId);
}
