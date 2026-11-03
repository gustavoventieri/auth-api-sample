package com.auth.api.framework.auth.adapter.port.out.persistence.repository;

import com.auth.api.framework.auth.adapter.port.out.persistence.entity.ResetPasswordVerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringResetPasswordVerificationTokenDataRepository
        extends JpaRepository<ResetPasswordVerificationTokenEntity, String> {

    Optional<ResetPasswordVerificationTokenEntity> findByUserId(String userId);

    void deleteByUserId(String userId);
}
