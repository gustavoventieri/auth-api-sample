package com.auth.api.framework.auth.utils.mfa;

import com.auth.api.core.auth.application.port.out.TotpPort;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.exceptions.TimeProviderException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.OptionalLong;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

/**
 * Implementação TOTP (RFC 6238) usando a lib dev.samstevens.totp.
 * Parâmetros fixos em SHA1 / 6 dígitos / 30s: é o padrão suportado por todos os apps
 * (Google Authenticator ignora algorithm/digits/period em versões antigas).
 */
@Component
public class TotpAdapter implements TotpPort {

    static final HashingAlgorithm ALGORITHM = HashingAlgorithm.SHA1;
    static final int DIGITS = 6;
    static final int PERIOD_SECONDS = 30;
    /** Aceita o passo atual e 1 passo antes/depois (±30s) para tolerar relógios dessincronizados. */
    static final int ALLOWED_TIME_STEP_DISCREPANCY = 1;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator(32); // 32 chars Base32 = 160 bits
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator(ALGORITHM, DIGITS);
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final RecoveryCodeGenerator recoveryCodeGenerator = new RecoveryCodeGenerator();
    private final TimeProvider timeProvider;
    private final String issuer;

    @Autowired
    public TotpAdapter(MfaProperties mfaProperties) {
        this(mfaProperties, new SystemTimeProvider());
    }

    /** Usado nos testes para fixar o relógio. */
    TotpAdapter(MfaProperties mfaProperties, TimeProvider timeProvider) {
        this.issuer = mfaProperties.issuer();
        this.timeProvider = timeProvider;
    }

    @Override
    public String generateSecret() {
        return secretGenerator.generate();
    }

    @Override
    public String buildOtpAuthUri(String secret, String accountName) {
        return qrData(secret, accountName).getUri();
    }

    @Override
    public String buildQrCodeDataUri(String secret, String accountName) {
        try {
            byte[] image = qrGenerator.generate(qrData(secret, accountName));
            return getDataUriForImage(image, qrGenerator.getImageMimeType());
        } catch (QrGenerationException ex) {
            throw new IllegalStateException("Could not generate TOTP QR code", ex);
        }
    }

    @Override
    public OptionalLong verifyCode(String secret, String code, Long lastUsedTimeStep) {
        if (code == null || !code.matches("\\d{" + DIGITS + "}")) {
            return OptionalLong.empty();
        }

        long currentStep = currentTimeStep();
        // Percorre a janela inteira sem retornar cedo para manter tempo constante.
        long matchedStep = -1;

        for (long step = currentStep - ALLOWED_TIME_STEP_DISCREPANCY;
             step <= currentStep + ALLOWED_TIME_STEP_DISCREPANCY;
             step++) {

            if (lastUsedTimeStep != null && step <= lastUsedTimeStep) {
                continue; // código já usado (ou anterior a ele): bloqueia replay
            }

            if (matchedStep < 0 && constantTimeEquals(generateCode(secret, step), code)) {
                matchedStep = step;
            }
        }

        return matchedStep >= 0 ? OptionalLong.of(matchedStep) : OptionalLong.empty();
    }

    @Override
    public List<String> generateRecoveryCodes(int amount) {
        return Arrays.stream(recoveryCodeGenerator.generateCodes(amount))
                .map(code -> code.toLowerCase(Locale.ROOT))
                .toList();
    }

    private QrData qrData(String secret, String accountName) {
        return new QrData.Builder()
                .label(issuer + ":" + accountName)
                .secret(secret)
                .issuer(issuer)
                .algorithm(ALGORITHM)
                .digits(DIGITS)
                .period(PERIOD_SECONDS)
                .build();
    }

    private long currentTimeStep() {
        try {
            return Math.floorDiv(timeProvider.getTime(), PERIOD_SECONDS);
        } catch (TimeProviderException ex) {
            throw new IllegalStateException("Could not read current time", ex);
        }
    }

    private String generateCode(String secret, long timeStep) {
        try {
            return codeGenerator.generate(secret, timeStep);
        } catch (CodeGenerationException ex) {
            throw new IllegalStateException("Could not generate TOTP code", ex);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }
}
