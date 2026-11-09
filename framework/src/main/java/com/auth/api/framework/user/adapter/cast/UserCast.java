package com.auth.api.framework.user.adapter.cast;

import com.auth.api.core.shared.enumerated.MfaStatus;
import com.auth.api.core.user.domain.UserDomain;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;

public class UserCast {

    public static UserEntity toEntity(UserDomain userDomain) {
        return UserEntity.builder()
                .id(userDomain.id())
                .name(userDomain.name())
                .email(userDomain.email())
                .password(userDomain.password())
                .role(userDomain.role())
                .verified(userDomain.isVerified())
                .active(userDomain.isActive())
                .mfaStatus(userDomain.mfaStatus() != null ? userDomain.mfaStatus() : MfaStatus.PENDING)
                .createdAt(userDomain.createdAt())
                .updatedAt(userDomain.updatedAt())
                .build();
    }

    public static UserDomain toDomain(UserEntity entity) {
        return new UserDomain(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.isVerified(),
                entity.isActive(),
                entity.getMfaStatus() != null ? entity.getMfaStatus() : MfaStatus.PENDING,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}