package com.auth.core.auth.application.port.in.sign;

import com.auth.core.auth.application.dto.response.AuthResponse;

public interface RefreshTokenUseCase {
    AuthResponse execute(String refreshToken);

}
