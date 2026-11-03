package com.auth.api.core.shared.util;

import com.auth.api.core.shared.exception.BusinessException;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requireNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(message);
        }
    }
}
