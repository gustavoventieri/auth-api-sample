package com.auth.core.user.domain;

import com.auth.core.shared.enumerated.Roles;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
public record User(
        String id,
        String name,
        String email,
        String password,
        Roles role,
        boolean isVerified,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static User create(
            String name,
            String email,
            String hashedPassword
    ) {

        LocalDateTime now = LocalDateTime.now();

        return User.builder()
                .id(UUID.randomUUID().toString())
                .name(formatName(name))
                .email(email)
                .password(hashedPassword)
                .role(Roles.USER)
                .isVerified(false)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public User verify() {
        return this.toBuilder()
                .isVerified(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User inactivate() {
        return this.toBuilder()
                .isActive(false)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User updateRegistration(
            String name,
            String hashedPassword
    ) {
        return this.toBuilder()
                .name(name)
                .password(hashedPassword)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private static String formatName(String name) {
        return Arrays.stream(name.trim().toLowerCase().split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}