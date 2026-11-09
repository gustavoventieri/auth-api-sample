package com.auth.api.framework.auth.utils.mfa;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @param issuer        nome exibido no app autenticador (ex.: "Financial API").
 * @param encryptionKey chave AES em Base64 (16, 24 ou 32 bytes) usada para criptografar os segredos TOTP.
 */
@ConfigurationProperties(prefix = "app.security.mfa")
public record MfaProperties(
        String issuer,
        String encryptionKey
) {
}
