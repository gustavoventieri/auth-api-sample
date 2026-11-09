package com.auth.api.framework.auth.adapter.port.out.persistence.repository;

import com.auth.api.framework.auth.adapter.port.out.persistence.entity.UserTotpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringUserTotpDataRepository extends JpaRepository<UserTotpEntity, String> {

    Optional<UserTotpEntity> findByUserId(String userId);

    void deleteByUserId(String userId);
}
