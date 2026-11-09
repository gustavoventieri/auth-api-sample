package com.auth.api.core.auth.application.port.in.mfa;

import com.auth.api.core.auth.application.dto.response.AdminMfaResetResponse;

public interface AdminResetMfaUseCase {
    AdminMfaResetResponse execute(String adminUserId, String targetUserId, String reason);
}
