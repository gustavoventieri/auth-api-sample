package com.auth.api.core.auth.application.port.in.mfa;

public interface HashRecoveryCodeUseCase {
    String execute(String value);
}
