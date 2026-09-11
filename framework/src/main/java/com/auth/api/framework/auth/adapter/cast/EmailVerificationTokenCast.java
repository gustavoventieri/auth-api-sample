package com.auth.api.framework.auth.adapter.cast;

import com.auth.core.auth.domain.EmailVerificationToken;
import com.auth.api.framework.auth.adapter.port.out.persistence.entity.EmailVerificationTokenEntity;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;

public class EmailVerificationTokenCast {

    public static EmailVerificationTokenEntity toEntity(
            EmailVerificationToken token
    ) {
        return EmailVerificationTokenEntity.builder()
                .id(token.id())
                .user(
                        UserEntity.builder()
                                .id(token.userId())
                                .build()
                )
                .otpCode(token.otpCode())
                .expiresAt(token.expiresAt())
                .createdAt(token.createdAt())
                .usedAt(token.usedAt())
                .build();
    }

    public static EmailVerificationToken toDomain(
            EmailVerificationTokenEntity entity
    ) {
        return new EmailVerificationToken(
                entity.getId(),
                entity.getUser().getId(),
                entity.getOtpCode(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUsedAt()
        );
    }

}