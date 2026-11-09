package com.auth.api.core.auth.application.port.out;

public interface SecretEncryptionPort {
    String encrypt(String plainText);
    String decrypt(String cipherText);
}
