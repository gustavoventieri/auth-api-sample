package com.auth.api.framework.shared.config.security;

public final class SecurityEndpoints {

    private SecurityEndpoints() {
    }

    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/sign-in",
            "/auth/sign-up",
            "/auth/verify-email",

            "/sessions/refresh",

            "/scalar",
            "/scalar/**",

            "/v3/api-docs",
            "/v3/api-docs/**"
    };

    public static final String[] CSRF_IGNORED_ENDPOINTS = {
            "/auth/sign-in",
            "/auth/sign-up",
            "/auth/verify-email",
            "/sessions/refresh",
            "/sessions/logout"
    };

    public static boolean isPublic(String path) {
        return path.equals("/auth/sign-in")
                || path.equals("/auth/sign-up")
                || path.equals("/auth/verify-email")
                || path.equals("/sessions/refresh")
                || path.equals("/scalar")
                || path.startsWith("/scalar/")
                || path.equals("/v3/api-docs")
                || path.startsWith("/v3/api-docs/");
    }
}