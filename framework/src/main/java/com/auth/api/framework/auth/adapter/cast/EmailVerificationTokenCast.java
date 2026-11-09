package com.auth.api.framework.auth.adapter.cast;


import com.auth.api.core.auth.domain.EmailVerificationTokenDomain;
import com.auth.api.framework.auth.adapter.port.out.persistence.entity.EmailVerificationTokenEntity;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;

public class EmailVerificationTokenCast {

    public static EmailVerificationTokenEntity toEntity(
            EmailVerificationTokenDomain token, UserEntity user
    ) {
        return EmailVerificationTokenEntity.builder()
                .id(token.id())
                .user(
                        user
                )
                .otpCode(token.otpCode())
                .expiresAt(token.expiresAt())
                .createdAt(token.createdAt())
                .usedAt(token.usedAt())
                .build();
    }

    public static EmailVerificationTokenDomain toDomain(
            EmailVerificationTokenEntity entity
    ) {
        return new EmailVerificationTokenDomain(
                entity.getId(),
                entity.getUser().getId(),
                entity.getOtpCode(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUsedAt()
        );
    }

}