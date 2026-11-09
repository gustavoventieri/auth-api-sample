package com.auth.api.core.shared.email;

public interface EmailTemplateUseCase {
    String buildVerificationEmail(String otpCode);
    String buildResetPasswordEmail(String otpCode);
    String buildPasswordChangedEmail(String name);
    String buildMfaEnabledEmail(String name);
    String buildAccountCreatedEmail(
            String name,
            String otpCode
    );

    String buildNewLoginEmail(
            String name,
            String device,
            String ip,
            String dateTime
    );
}
