package com.auth.api.core.auth.application.port.in.sign;

import com.auth.api.core.auth.application.dto.response.AuthResponse;

public interface RefreshTokenUseCase {
    AuthResponse execute(String refreshToken);

}
