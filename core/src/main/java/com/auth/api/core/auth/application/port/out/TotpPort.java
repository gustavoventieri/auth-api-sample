package com.auth.api.core.auth.application.port.out;

import java.util.List;
import java.util.OptionalLong;

/**
 * Porta para o algoritmo TOTP (RFC 6238). A implementação fica no framework.
 */
public interface TotpPort {

    /** Gera um novo segredo em Base32. */
    String generateSecret();

    /** Monta a URI otpauth:// lida pelos apps autenticadores. */
    String buildOtpAuthUri(String secret, String accountName);

    /** Gera o QR code da URI como data URI (data:image/png;base64,...). */
    String buildQrCodeDataUri(String secret, String accountName);

    /**
     * Valida o código dentro da janela de tolerância.
     *
     * @param lastUsedTimeStep último time step aceito (null se nunca usado); passos
     *                         iguais ou anteriores são recusados para evitar replay.
     * @return o time step que casou com o código, ou vazio se o código for inválido.
     */
    OptionalLong verifyCode(String secret, String code, Long lastUsedTimeStep);

    /** Gera códigos de recuperação em texto puro. */
    List<String> generateRecoveryCodes(int amount);
}
