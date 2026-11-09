package com.auth.api.core.user.domain;

import com.auth.api.core.shared.enumerated.MfaStatus;
import com.auth.api.core.shared.enumerated.Roles;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
public record UserDomain(
        String id,
        String name,
        String email,
        String password,
        Roles role,
        boolean isVerified,
        boolean isActive,
        MfaStatus mfaStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static UserDomain create(
            String name,
            String email,
            String hashedPassword
    ) {
        LocalDateTime now = LocalDateTime.now();

        return UserDomain.builder()
                .id(UUID.randomUUID().toString())
                .name(formatName(name))
                .email(email)
                .password(hashedPassword)
                .role(Roles.USER)
                .isVerified(false)
                .isActive(true)
                .mfaStatus(MfaStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public boolean isActiveAndVerified() {
        return isActive && isVerified;
    }

    public boolean isMfaEnabled() {
        return mfaStatus == MfaStatus.ENABLED;
    }

    public UserDomain enableMfa() {
        return touch().mfaStatus(MfaStatus.ENABLED).build();
    }

    public UserDomain verify() {
        return touch().isVerified(true).build();
    }

    public UserDomain inactivate() {
        return touch().isActive(false).build();
    }

    public UserDomain updateRegistration(String name, String hashedPassword) {
        return touch()
                .name(formatName(name))
                .password(hashedPassword)
                .build();
    }

    public UserDomain updatePassword(String hashedPassword) {
        return touch().password(hashedPassword).build();
    }

    private UserDomainBuilder touch() {
        return this.toBuilder().updatedAt(LocalDateTime.now());
    }

    private static String formatName(String name) {
        return Arrays.stream(name.trim().toLowerCase().split("\\s+"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
