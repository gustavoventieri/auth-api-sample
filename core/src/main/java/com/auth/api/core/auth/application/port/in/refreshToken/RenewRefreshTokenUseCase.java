package com.auth.api.core.auth.application.port.in.refreshToken;

import com.auth.api.core.auth.application.dto.response.AuthResponse;

public interface RenewRefreshTokenUseCase {
    AuthResponse execute(String refreshToken);

}
