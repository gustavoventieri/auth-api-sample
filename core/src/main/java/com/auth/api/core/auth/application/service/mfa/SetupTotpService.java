package com.auth.api.core.auth.application.service.mfa;

import com.auth.api.core.auth.application.dto.response.TotpSetupResponse;
import com.auth.api.core.auth.application.dto.response.ValidMfaChallenge;
import com.auth.api.core.auth.application.port.in.mfa.SetupTotpUseCase;
import com.auth.api.core.auth.application.port.out.SecretEncryptionPort;
import com.auth.api.core.auth.application.port.out.TotpPort;
import com.auth.api.core.auth.application.port.out.UserTotpPersistencePort;
import com.auth.api.core.auth.domain.MfaChallengePurpose;
import com.auth.api.core.auth.domain.UserTotpDomain;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.domain.UserDomain;

/**
 * Gera um novo segredo TOTP (ainda não confirmado) e devolve os dados para o
 * usuário vincular o app autenticador. Chamar de novo substitui o segredo pendente.
 */
public class SetupTotpService implements SetupTotpUseCase {

    private final MfaChallengeValidator mfaChallengeValidator;
    private final UserTotpPersistencePort userTotpPersistencePort;
    private final TotpPort totpPort;
    private final SecretEncryptionPort secretEncryptionPort;
    private final TransactionManager transactionManager;

    public SetupTotpService(
            MfaChallengeValidator mfaChallengeValidator,
            UserTotpPersistencePort userTotpPersistencePort,
            TotpPort totpPort,
            SecretEncryptionPort secretEncryptionPort,
            TransactionManager transactionManager
    ) {
        this.mfaChallengeValidator = mfaChallengeValidator;
        this.userTotpPersistencePort = userTotpPersistencePort;
        this.totpPort = totpPort;
        this.secretEncryptionPort = secretEncryptionPort;
        this.transactionManager = transactionManager;
    }

    @Override
    public TotpSetupResponse execute(String mfaToken) {
        ValidMfaChallenge valid = mfaChallengeValidator.validate(mfaToken, MfaChallengePurpose.SETUP);
        UserDomain user = valid.user();

        if (user.isMfaEnabled()) {
            throw new BusinessException("MFA is already enabled");
        }

        String secret = totpPort.generateSecret();

        UserTotpDomain pendingTotp = UserTotpDomain.create(
                user.id(),
                secretEncryptionPort.encrypt(secret)
        );

        transactionManager.run(() -> {
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
