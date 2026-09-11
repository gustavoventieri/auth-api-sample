package com.auth.api.framework.shared.config.security;

import com.auth.core.auth.application.port.in.accessToken.ExtractRoleFromAccessTokenUseCase;
import com.auth.core.auth.application.port.in.accessToken.ExtractUserIdFromAccessTokenUseCase;
import com.auth.core.auth.application.port.in.accessToken.ValidateAccessTokenUseCase;
import com.auth.core.shared.enumerated.Roles;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final static String ACCESS_TOKEN_NAME = "access_token";

    private final ValidateAccessTokenUseCase validateAccessTokenUseCase;
    private final ExtractUserIdFromAccessTokenUseCase extractUserIdFromAccessTokenUseCase;
    private final ExtractRoleFromAccessTokenUseCase extractRoleFromAccessTokenUseCase;


    public SecurityFilter(
            ValidateAccessTokenUseCase validateAccessTokenUseCase,
            ExtractUserIdFromAccessTokenUseCase extractUserIdFromAccessTokenUseCase,
            ExtractRoleFromAccessTokenUseCase extractRoleFromAccessTokenUseCase
    ) {
        this.validateAccessTokenUseCase = validateAccessTokenUseCase;
        this.extractUserIdFromAccessTokenUseCase = extractUserIdFromAccessTokenUseCase;
        this.extractRoleFromAccessTokenUseCase = extractRoleFromAccessTokenUseCase;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.getAccessToken(request);
        boolean tokenIsValid = validateAccessTokenUseCase.execute(token);

        if (tokenIsValid) {
            String userId = extractUserIdFromAccessTokenUseCase.execute(token);
            Roles role = extractRoleFromAccessTokenUseCase.execute(token);

            Collection<SimpleGrantedAuthority> grantedRoles = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, grantedRoles);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI()
                .substring(request.getContextPath().length());

        return SecurityEndpoints.isPublic(path);
    }


    private String getAccessToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();


        for (Cookie cookie : cookies) {

            if (ACCESS_TOKEN_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }


        return null;
    }
}
