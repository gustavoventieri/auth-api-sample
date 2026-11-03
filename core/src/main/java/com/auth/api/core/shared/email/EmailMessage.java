package com.auth.api.core.shared.email;

public record EmailMessage(
        String recipient,
        String subject,
        String html
) {}