package com.auth.api.framework.shared.config.security;

import com.auth.api.framework.shared.config.security.annotations.RequiredRole;
import com.auth.api.framework.shared.config.security.annotations.RequiredRoleAuthorizationManager;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;

@Configuration
public class MethodSecurityConfig {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static RequiredRoleAuthorizationManager requiredRoleAuthorizationManager() {
        return new RequiredRoleAuthorizationManager();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static AuthorizationManagerBeforeMethodInterceptor requiredRoleInterceptor(
            RequiredRoleAuthorizationManager authorizationManager
    ) {

        AnnotationMatchingPointcut pointcut =
                new AnnotationMatchingPointcut(
                        null,
                        RequiredRole.class,
                        true
                );

        return new AuthorizationManagerBeforeMethodInterceptor(
                pointcut,
                authorizationManager
        );
    }
}