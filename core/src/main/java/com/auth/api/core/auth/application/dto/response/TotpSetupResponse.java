package com.auth.api.core.auth.application.dto.response;

public record TotpSetupResponse(
        String secret,
        String otpAuthUri,
        String qrCodeDataUri
) {
}
