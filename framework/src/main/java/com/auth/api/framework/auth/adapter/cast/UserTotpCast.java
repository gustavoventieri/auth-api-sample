package com.auth.api.framework.auth.adapter.cast;

import com.auth.api.core.auth.domain.UserTotpDomain;
import com.auth.api.framework.auth.adapter.port.out.persistence.entity.UserTotpEntity;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;

public class UserTotpCast {

    public static UserTotpEntity toEntity(UserTotpDomain totp, UserEntity user) {
        return UserTotpEntity.builder()
                .id(totp.id())
                .user(user)
                .encryptedSecret(totp.encryptedSecret())
                .lastUsedTimeStep(totp.lastUsedTimeStep())
                .confirmedAt(totp.confirmedAt())
                .createdAt(totp.createdAt())
                .version(totp.version())
                .updatedAt(totp.updatedAt())
                .build();
    }

    public static UserTotpDomain toDomain(UserTotpEntity entity) {
        return new UserTotpDomain(
                entity.getId(),
                entity.getUser().getId(),
                entity.getEncryptedSecret(),
                entity.getLastUsedTimeStep(),
                entity.getConfirmedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
