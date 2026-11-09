package com.auth.api.core.auth.domain;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Segredo TOTP do usuário. O segredo fica criptografado (não pode ser hasheado,
 * pois o servidor precisa dele para calcular o código esperado).
 * lastUsedTimeStep impede que o mesmo código seja reutilizado (replay).
 */
@Builder(toBuilder = true)
public record UserTotpDomain(
        String id,
        String userId,
        String encryptedSecret,
        Long lastUsedTimeStep,
        LocalDateTime confirmedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        /** Versão para lock otimista (null = ainda não persistido). */
        Long version
) {

    public static UserTotpDomain create(String userId, String encryptedSecret) {
        LocalDateTime now = LocalDateTime.now();

        return UserTotpDomain.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .encryptedSecret(encryptedSecret)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public boolean isConfirmed() {
        return confirmedAt != null;
    }

    public UserTotpDomain confirm(long timeStep) {
        LocalDateTime now = LocalDateTime.now();

        return this.toBuilder()
                .confirmedAt(now)
                .lastUsedTimeStep(timeStep)
                .updatedAt(now)
                .build();
    }

    public UserTotpDomain registerUse(long timeStep) {
        return this.toBuilder()
                .lastUsedTimeStep(timeStep)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
