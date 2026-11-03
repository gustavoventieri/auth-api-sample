package com.auth.api.framework.shared.config.security.annotations;

import com.auth.api.core.shared.enumerated.Roles;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredRole {

    Roles[] value();
}