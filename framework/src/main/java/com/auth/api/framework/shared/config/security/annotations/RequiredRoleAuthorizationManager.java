package com.auth.api.framework.shared.config.security.annotations;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.function.Supplier;

public class RequiredRoleAuthorizationManager
        implements AuthorizationManager<MethodInvocation> {

    @Override
    public AuthorizationDecision authorize(
            Supplier<? extends Authentication> authentication,
            MethodInvocation invocation
    ) {

        RequiredRole requiredRole =
                invocation.getMethod()
                        .getAnnotation(RequiredRole.class);

        if (requiredRole == null) {
            return new AuthorizationDecision(true);
        }

        Authentication auth = authentication.get();

        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        boolean authorized = Arrays.stream(requiredRole.value())
                .anyMatch(required ->
                        auth.getAuthorities()
                                .stream()
                                .anyMatch(authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_" + required.name())
                                )
                );

        return new AuthorizationDecision(authorized);
    }
}