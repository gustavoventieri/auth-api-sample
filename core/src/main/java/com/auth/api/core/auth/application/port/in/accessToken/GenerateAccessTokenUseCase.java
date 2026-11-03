package com.auth.api.core.auth.application.port.in.accessToken;

import com.auth.api.core.shared.enumerated.Roles;

public interface GenerateAccessTokenUseCase {
    String execute(String userId, Roles role);
}
