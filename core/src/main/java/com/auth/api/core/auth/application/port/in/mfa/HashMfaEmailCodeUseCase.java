package com.auth.api.core.auth.application.port.in.mfa;

public interface HashMfaEmailCodeUseCase {
    String execute(String code);
}
