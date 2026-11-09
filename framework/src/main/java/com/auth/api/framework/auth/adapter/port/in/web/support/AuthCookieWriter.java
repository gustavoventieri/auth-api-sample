package com.auth.api.framework.auth.adapter.port.in.web.support;

import com.auth.api.core.auth.application.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookieWriter {

    public static final String ACCESS_TOKEN_NAME = "access_token";
    public static final String REFRESH_TOKEN_NAME = "refresh_token";

    public void addAuthCookies(HttpServletResponse response, AuthResponse authResponse) {
        response.addHeader("Set-Cookie", cookie(ACCESS_TOKEN_NAME, authResponse.accessToken(), Duration.ofMinutes(15)));
        response.addHeader("Set-Cookie", cookie(REFRESH_TOKEN_NAME, authResponse.refreshToken(), Duration.ofDays(30)));
    }

    private static String cookie(String name, String value, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build()
                .toString();
    }
}
