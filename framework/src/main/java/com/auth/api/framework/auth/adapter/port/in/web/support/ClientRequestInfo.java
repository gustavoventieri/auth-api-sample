package com.auth.api.framework.auth.adapter.port.in.web.support;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientRequestInfo {

    private ClientRequestInfo() {
    }

    public static String device(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String ip(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
