package com.auth.api.framework.shared.config.security;

public final class SecurityEndpoints {

    private SecurityEndpoints() {
    }

    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/sign-in",
            "/auth/sign-up",
            "/auth/verify-email",
            "/auth/password/forgot",
            "/auth/password/verify",
            "/auth/password/reset",
            "/auth/mfa/totp/setup",
            "/auth/mfa/totp/setup/confirm",
            "/auth/mfa/totp/verify",
            "/auth/mfa/recovery",

            "/sessions/refresh",

            "/scalar",
            "/scalar/**",

            "/v3/api-docs",
            "/v3/api-docs/**",

            "/actuator/health",
            "/actuator/health/**"
    };

    public static final String[] CSRF_IGNORED_ENDPOINTS = {
            "/auth/sign-in",
            "/auth/sign-up",
            "/auth/verify-email",
            "/auth/password/forgot",
            "/auth/password/verify",
            "/auth/password/reset",
            "/auth/mfa/totp/setup",
            "/auth/mfa/totp/setup/confirm",
            "/auth/mfa/totp/verify",
            "/auth/mfa/recovery",
            "/sessions/refresh",
            "/sessions/logout"
    };

    public static boolean isPublic(String path) {
        return path.equals("/auth/sign-in")
                || path.equals("/auth/sign-up")
                || path.equals("/auth/verify-email")
                || path.equals("/auth/password/forgot")
                || path.equals("/auth/password/verify")
                || path.equals("/auth/password/reset")
                || path.equals("/auth/mfa/totp/setup")
                || path.equals("/auth/mfa/totp/setup/confirm")
                || path.equals("/auth/mfa/totp/verify")
                || path.equals("/auth/mfa/recovery")
                || path.equals("/sessions/refresh")
                || path.equals("/scalar")
                || path.startsWith("/scalar/")
                || path.equals("/v3/api-docs")
                || path.startsWith("/v3/api-docs/")
                || path.equals("/actuator/health")
                || path.startsWith("/actuator/health/");
    }
}
