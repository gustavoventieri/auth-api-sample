package com.auth.api.core.auth.application.port.in.sign;

import com.auth.api.core.auth.application.dto.response.AuthResponse;

public interface SignInUseCase {
    AuthResponse execute(String email, String password, String ip, String device);
}
