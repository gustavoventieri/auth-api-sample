package com.auth.api.core.auth.application.dto.response;

import com.auth.api.core.auth.domain.ResetPasswordVerificationTokenDomain;
import com.auth.api.core.user.domain.UserDomain;

public record ValidResetPasswordToken(
        UserDomain user,
        ResetPasswordVerificationTokenDomain token) {
}
