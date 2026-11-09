package com.auth.api.core.auth.domain;

public enum MfaChallengePurpose {
    /** O usuário ainda não tem TOTP: o desafio só permite configurar o autenticador. */
    SETUP,
    /** O usuário já tem TOTP: o desafio só permite validar o código e concluir o login. */
    VERIFICATION
}
