package com.auth.api.framework.auth.utils.password;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.bcrypt")
public record BCryptProperties(
        int strength
) {
}