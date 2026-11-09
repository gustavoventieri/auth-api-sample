package com.auth.api.framework.auth.utils.mfa;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AesGcmSecretEncryptionAdapterTest {

    private static String key(int bytes) {
        return Base64.getEncoder().encodeToString(new byte[bytes]);
    }

    private final AesGcmSecretEncryptionAdapter adapter =
            new AesGcmSecretEncryptionAdapter(new MfaProperties("Financial API", key(32)));

    @Test
    void encryptsAndDecryptsRoundTrip() {
        String secret = "JBSWY3DPEHPK3PXPJBSWY3DPEHPK3PXP";
        String encrypted = adapter.encrypt(secret);

        assertNotEquals(secret, encrypted);
        assertEquals(secret, adapter.decrypt(encrypted));
    }

    @Test
    void usesRandomIvSoSameInputProducesDifferentOutput() {
        assertNotEquals(adapter.encrypt("same"), adapter.encrypt("same"));
    }

    @Test
    void failsWhenCipherTextIsTampered() {
        byte[] raw = Base64.getDecoder().decode(adapter.encrypt("secret"));
        raw[raw.length - 1] ^= 1;

        assertThrows(IllegalStateException.class, () -> adapter.decrypt(Base64.getEncoder().encodeToString(raw)));
    }

    @Test
    void failsWithAnotherKey() {
        AesGcmSecretEncryptionAdapter other =
                new AesGcmSecretEncryptionAdapter(new MfaProperties("x", Base64.getEncoder().encodeToString("0123456789abcdef0123456789abcdef".getBytes())));

        assertThrows(IllegalStateException.class, () -> other.decrypt(adapter.encrypt("secret")));
    }

    @Test
    void rejectsMissingOrInvalidKeys() {
        assertThrows(IllegalStateException.class, () -> new AesGcmSecretEncryptionAdapter(new MfaProperties("x", null)));
        assertThrows(IllegalStateException.class, () -> new AesGcmSecretEncryptionAdapter(new MfaProperties("x", "not-base64!")));
        assertThrows(IllegalStateException.class, () -> new AesGcmSecretEncryptionAdapter(new MfaProperties("x", key(10))));
    }
}
