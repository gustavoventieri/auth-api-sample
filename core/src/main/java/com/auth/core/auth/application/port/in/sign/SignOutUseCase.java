package com.auth.core.auth.application.port.in.sign;

public interface SignOutUseCase {
    void execute(String refreshToken);
}
