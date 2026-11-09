package com.auth.api.framework.auth.adapter.dto.response;

import java.util.List;

/**
 * Exibidos uma única vez. O usuário deve guardá-los em local seguro.
 */
public record RecoveryCodesResponseDTO(
        List<String> recoveryCodes
) {
}
