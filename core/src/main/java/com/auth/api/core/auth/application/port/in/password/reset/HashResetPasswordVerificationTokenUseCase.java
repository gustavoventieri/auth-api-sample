package com.auth.api.core.auth.application.port.in.password.reset;

public interface HashResetPasswordVerificationTokenUseCase {
    String execute(String token);
}
