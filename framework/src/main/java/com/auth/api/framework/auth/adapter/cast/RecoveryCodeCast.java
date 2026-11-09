package com.auth.api.framework.auth.adapter.cast;

import com.auth.api.core.auth.domain.RecoveryCodeDomain;
import com.auth.api.framework.auth.adapter.port.out.persistence.entity.RecoveryCodeEntity;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;

public class RecoveryCodeCast {

    public static RecoveryCodeEntity toEntity(RecoveryCodeDomain recoveryCode) {
        return RecoveryCodeEntity.builder()
                .id(recoveryCode.id())
                .user(UserEntity.builder().id(recoveryCode.userId()).build())
                .codeHash(recoveryCode.codeHash())
                .createdAt(recoveryCode.createdAt())
                .usedAt(recoveryCode.usedAt())
                .build();
    }

    public static RecoveryCodeDomain toDomain(RecoveryCodeEntity entity) {
        return new RecoveryCodeDomain(
                entity.getId(),
                entity.getUser().getId(),
                entity.getCodeHash(),
                entity.getCreatedAt(),
                entity.getUsedAt()
        );
    }
}
