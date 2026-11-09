package com.auth.api.framework.auth.utils.token;

import com.auth.api.core.auth.application.port.in.mfa.HashMfaChallengeTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.HashRecoveryCodeUseCase;
import com.auth.api.framework.auth.utils.hash.Hasher256Service;
import org.springframework.stereotype.Component;

/**
 * SHA-256 é suficiente aqui: tanto o mfaToken (256 bits) quanto os códigos de
 * recuperação (16 caracteres aleatórios) têm entropia alta demais para força bruta offline.
 */
@Component
public class MfaHasher implements HashMfaChallengeTokenUseCase, HashRecoveryCodeUseCase {

    private final Hasher256Service sha256Hasher;

    public MfaHasher(Hasher256Service sha256Hasher) {
        this.sha256Hasher = sha256Hasher;
    }

    @Override
    public String execute(String value) {
        return sha256Hasher.hash(value);
    }
}
