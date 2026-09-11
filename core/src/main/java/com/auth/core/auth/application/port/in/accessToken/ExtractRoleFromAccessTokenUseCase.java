package com.auth.core.auth.application.port.in.accessToken;

import com.auth.core.shared.enumerated.Roles;

public interface ExtractRoleFromAccessTokenUseCase {
    Roles execute(String token);
}
