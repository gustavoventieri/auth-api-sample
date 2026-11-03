package com.auth.api.framework.auth.adapter.cast;

import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;
import com.auth.api.framework.auth.adapter.port.out.persistence.entity.ResetPasswordVerificationTokenEntity;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;

public class ResetPasswordVerificationTokenCast {

    public static ResetPasswordVerificationTokenEntity toEntity(ResetPasswordVerificationTokenDomain token) {
        return ResetPasswordVerificationTokenEntity.builder()
                .id(token.id())
                .user(UserEntity.builder().id(token.userId()).build())
                .otpCode(token.otpCode())
                .expiresAt(token.expiresAt())
                .createdAt(token.createdAt())
                .usedAt(token.usedAt())
                .build();
    }

    public static ResetPasswordVerificationTokenDomain toDomain(ResetPasswordVerificationTokenEntity entity) {
        return new ResetPasswordVerificationTokenDomain(
                entity.getId(),
                entity.getUser().getId(),
                entity.getOtpCode(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUsedAt()
        );
    }
}
