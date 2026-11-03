package com.auth.api.core.auth.application.port.in.sign;

import com.auth.api.core.auth.application.dto.response.AuthResponse;
import com.auth.api.core.user.domain.UserDomain;

public interface IssueAuthTokensUseCase {
    AuthResponse execute(UserDomain user, String ip, String device);
}
