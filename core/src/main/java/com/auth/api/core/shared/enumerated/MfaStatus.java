package com.auth.api.core.shared.enumerated;

/**
 * Estado do MFA (TOTP) do usuário.
 * PENDING  -> o usuário ainda não vinculou um app autenticador (setup obrigatório no próximo login).
 * ENABLED  -> o TOTP está ativo e é exigido em todo login.
 */
public enum MfaStatus {
    PENDING,
    ENABLED
}
