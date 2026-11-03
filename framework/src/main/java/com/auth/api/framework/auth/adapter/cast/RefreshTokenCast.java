package com.auth.api.framework.auth.adapter.cast;

import com.auth.api.core.auth.domain.RefreshTokenDomain;
import com.auth.api.framework.auth.adapter.port.out.persistence.entity.RefreshTokenEntity;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;

public class RefreshTokenCast {

    public static RefreshTokenEntity toEntity(RefreshTokenDomain token) {

        return RefreshTokenEntity.builder()
                .id(token.id())
                .user(
                        UserEntity.builder()
                                .id(token.userId())
                                .build()
                )
                .tokenHash(token.tokenHash())
                .ip(token.ip())
                .device(token.device())
                .createdAt(token.createdAt())
                .expiresAt(token.expiresAt())
                .revokedAt(token.revokedAt())
                .publicId(token.publicId())
                .build();
    }

    public static RefreshTokenDomain toDomain(RefreshTokenEntity entity) {

        return new RefreshTokenDomain(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTokenHash(),
                entity.getIp(),
                entity.getDevice(),
                entity.getPublicId(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getRevokedAt()
        );
    }


}