package com.auth.api.core.auth.domain;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Código de recuperação de uso único, usado quando o usuário perde o autenticador.
 * Apenas o hash é persistido.
 */
@Builder(toBuilder = true)
public record RecoveryCodeDomain(
        String id,
        String userId,
        String codeHash,
        LocalDateTime createdAt,
        LocalDateTime usedAt,
        /** Versão para lock otimista (null = ainda não persistido). */
        Long version
) {

    public static RecoveryCodeDomain create(String userId, String codeHash) {
        return RecoveryCodeDomain.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .codeHash(codeHash)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public RecoveryCodeDomain markAsUsed() {
        return this.toBuilder()
                .usedAt(LocalDateTime.now())
                .build();
    }
}
