package com.auth.api.core.auth.application.port.in.sign;

public interface SignOutUseCase {
    void execute(String refreshToken);
}
