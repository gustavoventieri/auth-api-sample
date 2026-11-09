package com.auth.api.framework.auth.adapter.port.out.persistence.repository;

import com.auth.api.framework.auth.adapter.port.out.persistence.entity.MfaChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringMfaChallengeDataRepository extends JpaRepository<MfaChallengeEntity, String> {

    Optional<MfaChallengeEntity> findByTokenHash(String tokenHash);

    void deleteByUserId(String userId);
}
