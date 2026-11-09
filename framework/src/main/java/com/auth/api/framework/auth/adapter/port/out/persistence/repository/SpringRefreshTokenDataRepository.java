package com.auth.api.framework.auth.adapter.port.out.persistence.repository;

import com.auth.api.framework.auth.adapter.port.out.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SpringRefreshTokenDataRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update RefreshTokenEntity r
               set r.revokedAt = :now
             where r.user.id = :userId
               and r.revokedAt is null
            """)
    int revokeAllActiveByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    Optional<RefreshTokenEntity> findByPublicIdAndUser_Id(
            String publicId,
            String userId
    );
}
