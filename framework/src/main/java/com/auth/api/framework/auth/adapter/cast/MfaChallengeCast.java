package com.auth.api.framework.auth.adapter.cast;

import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.framework.auth.adapter.port.out.persistence.entity.MfaChallengeEntity;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;

public class MfaChallengeCast {

    public static MfaChallengeEntity toEntity(MfaChallengeDomain challenge) {
        return MfaChallengeEntity.builder()
                .id(challenge.id())
                .user(UserEntity.builder().id(challenge.userId()).build())
                .tokenHash(challenge.tokenHash())
                .purpose(challenge.purpose())
                .failedAttempts(challenge.failedAttempts())
                .expiresAt(challenge.expiresAt())
                .createdAt(challenge.createdAt())
                .usedAt(challenge.usedAt())
                .build();
    }

    public static MfaChallengeDomain toDomain(MfaChallengeEntity entity) {
        return new MfaChallengeDomain(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTokenHash(),
                entity.getPurpose(),
                entity.getFailedAttempts(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUsedAt()
        );
    }
}
