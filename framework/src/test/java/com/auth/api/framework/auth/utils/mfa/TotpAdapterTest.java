package com.auth.api.framework.auth.utils.mfa;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class TotpAdapterTest {

    private static final String SECRET = "JBSWY3DPEHPK3PXPJBSWY3DPEHPK3PXP";
    private static final long NOW = 1_800_000_000L; // segundos (fixo para o teste)
    private static final long STEP = NOW / 30;

    private final AtomicLong clock = new AtomicLong(NOW);
    private final TotpAdapter adapter = new TotpAdapter(new MfaProperties("Financial API", null), clock::get);

    private static String codeAt(long step) throws Exception {
        return new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6).generate(SECRET, step);
    }

    @Test
    void acceptsCurrentCodeAndReturnsItsTimeStep() throws Exception {
        assertEquals(OptionalLong.of(STEP), adapter.verifyCode(SECRET, codeAt(STEP), null));
    }

    @Test
    void toleratesOneStepOfClockDrift() throws Exception {
        assertEquals(OptionalLong.of(STEP - 1), adapter.verifyCode(SECRET, codeAt(STEP - 1), null));
        assertEquals(OptionalLong.of(STEP + 1), adapter.verifyCode(SECRET, codeAt(STEP + 1), null));
    }

    @Test
    void rejectsCodesOutsideTheWindow() throws Exception {
        assertTrue(adapter.verifyCode(SECRET, codeAt(STEP - 2), null).isEmpty());
        assertTrue(adapter.verifyCode(SECRET, codeAt(STEP + 2), null).isEmpty());
    }

    @Test
    void rejectsReplayOfAnAlreadyUsedTimeStep() throws Exception {
        assertTrue(adapter.verifyCode(SECRET, codeAt(STEP), STEP).isEmpty());
        assertTrue(adapter.verifyCode(SECRET, codeAt(STEP - 1), STEP).isEmpty());
        assertEquals(OptionalLong.of(STEP + 1), adapter.verifyCode(SECRET, codeAt(STEP + 1), STEP));
    }

    @Test
    void rejectsMalformedCodes() {
        assertTrue(adapter.verifyCode(SECRET, null, null).isEmpty());
        assertTrue(adapter.verifyCode(SECRET, "12345", null).isEmpty());
        assertTrue(adapter.verifyCode(SECRET, "abcdef", null).isEmpty());
        assertTrue(adapter.verifyCode(SECRET, "1234567", null).isEmpty());
    }

    @Test
    void buildsGoogleAuthenticatorCompatibleUri() {
        String uri = adapter.buildOtpAuthUri(SECRET, "user@email.com");

        assertTrue(uri.startsWith("otpauth://totp/Financial%20API%3Auser%40email.com?"), uri);
        assertTrue(uri.contains("secret=" + SECRET));
        assertTrue(uri.contains("issuer=Financial%20API"));
        assertTrue(uri.contains("algorithm=SHA1"));
        assertTrue(uri.contains("digits=6"));
        assertTrue(uri.contains("period=30"));
    }

    @Test
    void generatesBase32SecretWith160Bits() {
        assertTrue(adapter.generateSecret().matches("[A-Z2-7]{32}"));
    }

    @Test
    void generatesQrCodeAsPngDataUri() {
        assertTrue(adapter.buildQrCodeDataUri(SECRET, "user@email.com").startsWith("data:image/png;base64,"));
    }

    @Test
    void generatesDistinctLowercaseRecoveryCodes() {
        List<String> codes = adapter.generateRecoveryCodes(10);

        assertEquals(10, codes.size());
        assertEquals(10, codes.stream().distinct().count());
        codes.forEach(code -> assertEquals(code.toLowerCase(), code));
    }
}
