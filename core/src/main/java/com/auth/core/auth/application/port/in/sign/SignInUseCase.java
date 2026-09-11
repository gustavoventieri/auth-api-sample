package com.auth.core.auth.application.port.in.sign;

import com.auth.core.auth.application.dto.response.AuthResponse;

public interface SignInUseCase {
    AuthResponse execute(String email, String password, String ip, String device);
}
