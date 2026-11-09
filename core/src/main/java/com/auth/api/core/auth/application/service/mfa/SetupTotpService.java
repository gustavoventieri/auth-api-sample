package com.auth.api.core.auth.application.service.mfa;

import com.auth.api.core.auth.application.dto.response.TotpSetupResponse;
import com.auth.api.core.auth.application.dto.response.ValidMfaChallenge;
import com.auth.api.core.auth.application.port.in.mfa.HashMfaEmailCodeUseCase;
import com.auth.api.core.auth.application.port.in.mfa.SetupTotpUseCase;
import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.application.port.out.SecretEncryptionPort;
import com.auth.api.core.auth.application.port.out.TotpPort;
import com.auth.api.core.auth.application.port.out.UserTotpPersistencePort;
import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.core.auth.domain.MfaChallengePurpose;
import com.auth.api.core.auth.domain.UserTotpDomain;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.domain.UserDomain;

import static com.auth.api.core.shared.util.SecurityUtils.constantTimeEquals;

/**
 * Gera um novo segredo TOTP (ainda não confirmado) e devolve os dados para o
 * usuário vincular o app autenticador. Chamar de novo substitui o segredo pendente.
 *
 * Se o desafio veio do sign-in (só senha), o código enviado por e-mail precisa ser informado
 * na primeira chamada; depois de confirmado, as chamadas seguintes não pedem de novo.
 */
public class SetupTotpService implements SetupTotpUseCase {

    private final MfaChallengeValidator mfaChallengeValidator;
    private final MfaChallengePersistencePort mfaChallengePersistencePort;
    private final HashMfaEmailCodeUseCase hashMfaEmailCodeUseCase;
    private final UserTotpPersistencePort userTotpPersistencePort;
    private final TotpPort totpPort;
    private final SecretEncryptionPort secretEncryptionPort;
    private final TransactionManager transactionManager;

    public SetupTotpService(
            MfaChallengeValidator mfaChallengeValidator,
            MfaChallengePersistencePort mfaChallengePersistencePort,
            HashMfaEmailCodeUseCase hashMfaEmailCodeUseCase,
            UserTotpPersistencePort userTotpPersistencePort,
            TotpPort totpPort,
            SecretEncryptionPort secretEncryptionPort,
            TransactionManager transactionManager
    ) {
        this.mfaChallengeValidator = mfaChallengeValidator;
        this.mfaChallengePersistencePort = mfaChallengePersistencePort;
        this.hashMfaEmailCodeUseCase = hashMfaEmailCodeUseCase;
        this.userTotpPersistencePort = userTotpPersistencePort;
        this.totpPort = totpPort;
        this.secretEncryptionPort = secretEncryptionPort;
        this.transactionManager = transactionManager;
    }

    @Override
    public TotpSetupResponse execute(String mfaToken, String emailCode) {
        ValidMfaChallenge valid = mfaChallengeValidator.validate(mfaToken, MfaChallengePurpose.SETUP);
        UserDomain user = valid.user();

        if (user.isMfaEnabled()) {
            throw new BusinessException("MFA is already enabled");
        }

        MfaChallengeDomain challenge = valid.challenge();
        boolean confirmingEmail = challenge.requiresEmailCode();

        if (confirmingEmail) {
            if (emailCode == null || emailCode.isBlank()) {
                throw new BusinessException("Email code is required to set up MFA. Check your inbox");
            }

            if (!constantTimeEquals(hashMfaEmailCodeUseCase.execute(emailCode.trim()), challenge.emailCodeHash())) {
                throw mfaChallengeValidator.registerFailedAttempt(challenge);
            }
        }

        MfaChallengeDomain emailConfirmedChallenge = confirmingEmail ? challenge.confirmEmail() : challenge;

        String secret = totpPort.generateSecret();

        UserTotpDomain pendingTotp = UserTotpDomain.create(
                user.id(),
                secretEncryptionPort.encrypt(secret)
        );

        transactionManager.run(() -> {
            if (confirmingEmail) {
                mfaChallengePersistencePort.save(emailConfirmedChallenge);
            }
            userTotpPersistencePort.deleteByUserId(user.id());
            userTotpPersistencePort.save(pendingTotp);
        });

        return new TotpSetupResponse(
                secret,
                totpPort.buildOtpAuthUri(secret, user.email()),
                totpPort.buildQrCodeDataUri(secret, user.email())
        );
    }
}
