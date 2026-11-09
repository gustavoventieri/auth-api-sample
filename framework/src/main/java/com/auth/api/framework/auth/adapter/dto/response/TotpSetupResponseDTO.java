package com.auth.api.framework.auth.adapter.dto.response;

import com.auth.api.core.auth.application.dto.response.TotpSetupResponse;

/**
 * @param secret     segredo em Base32 para digitação manual no app.
 * @param otpAuthUri URI otpauth:// (o front pode gerar o QR a partir dela).
 * @param qrCode     QR code pronto em data URI PNG (pode ser usado direto em &lt;img src&gt;).
 */
public record TotpSetupResponseDTO(
        String secret,
        String otpAuthUri,
        String qrCode
) {

    public static TotpSetupResponseDTO from(TotpSetupResponse response) {
        return new TotpSetupResponseDTO(response.secret(), response.otpAuthUri(), response.qrCodeDataUri());
    }
}
