package com.auth.api.framework.auth.utils.mfa;

import com.auth.api.core.auth.application.port.out.SecretEncryptionPort;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Criptografa os segredos TOTP com AES-GCM.
 * Formato persistido: Base64( IV[12 bytes] || ciphertext+tag ).
 */
@Component
public class AesGcmSecretEncryptionAdapter implements SecretEncryptionPort {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SecretKey key;

    public AesGcmSecretEncryptionAdapter(MfaProperties mfaProperties) {
        this.key = buildKey(mfaProperties.encryptionKey());
    }

    @Override
    public String encrypt(String plainText) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(
                    ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array()
            );
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Could not encrypt secret", ex);
        }
    }

    @Override
    public String decrypt(String encoded) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(encoded));

            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));

            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new IllegalStateException("Could not decrypt secret", ex);
        }
    }

    private static SecretKey buildKey(String base64Key) {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException("app.security.mfa.encryption-key (MFA_ENCRYPTION_KEY) is not configured");
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(base64Key.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("MFA_ENCRYPTION_KEY must be Base64 encoded", ex);
        }

        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalStateException("MFA_ENCRYPTION_KEY must decode to 16, 24 or 32 bytes (use 32)");
        }

        return new SecretKeySpec(keyBytes, "AES");
    }
}
