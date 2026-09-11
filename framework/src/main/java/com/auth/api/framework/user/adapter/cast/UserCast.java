package com.auth.api.framework.user.adapter.cast;

import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;
import com.auth.core.user.domain.User;

public class UserCast {

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.id())
                .name(user.name())
                .email(user.email())
                .password(user.password())
                .role(user.role())
                .verified(user.isVerified())
                .active(user.isActive())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.isVerified(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}