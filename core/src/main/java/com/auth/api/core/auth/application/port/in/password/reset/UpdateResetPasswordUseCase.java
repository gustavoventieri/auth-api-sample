package com.auth.api.core.auth.application.port.in.password.reset;

public interface UpdateResetPasswordUseCase {
    void execute(String email, String otpCode, String newPassword);
}
