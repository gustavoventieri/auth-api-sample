package com.auth.api.core.auth.application.service.mfa;

import com.auth.api.core.auth.application.service.sign.SignInNotifier;
import com.auth.api.core.auth.application.dto.response.AuthResponse;
import com.auth.api.core.auth.application.dto.response.ValidMfaChallenge;
import com.auth.api.core.auth.application.port.in.mfa.VerifyTotpUseCase;
import com.auth.api.core.auth.application.port.in.sign.IssueAuthTokensUseCase;
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

import static com.auth.api.core.shared.util.ValidationUtils.requireNotBlank;

/**
 * Segunda etapa do login: valida o código TOTP e emite a sessão.
 */
public class VerifyTotpService implements VerifyTotpUseCase {

    private final MfaChallengeValidator mfaChallengeValidator;
    private final MfaChallengePersistencePort mfaChallengePersistencePort;
    private final UserTotpPersistencePort userTotpPersistencePort;
    private final TotpPort totpPort;
    private final SecretEncryptionPort secretEncryptionPort;
    private final IssueAuthTokensUseCase issueAuthTokensUseCase;
    private final TransactionManager transactionManager;
    private final SignInNotifier signInNotifier;

    public VerifyTotpService(
            MfaChallengeValidator mfaChallengeValidator,
            MfaChallengePersistencePort mfaChallengePersistencePort,
            UserTotpPersistencePort userTotpPersistencePort,
            TotpPort totpPort,
            SecretEncryptionPort secretEncryptionPort,
            IssueAuthTokensUseCase issueAuthTokensUseCase,
            TransactionManager transactionManager,
            SignInNotifier signInNotifier
    ) {
        this.mfaChallengeValidator = mfaChallengeValidator;
        this.mfaChallengePersistencePort = mfaChallengePersistencePort;
        this.userTotpPersistencePort = userTotpPersistencePort;
        this.totpPort = totpPort;
        this.secretEncryptionPort = secretEncryptionPort;
        this.issueAuthTokensUseCase = issueAuthTokensUseCase;
        this.transactionManager = transactionManager;
        this.signInNotifier = signInNotifier;
    }

    @Override
    public AuthResponse execute(String mfaToken, String code, String ip, String device) {
        requireNotBlank(code, "MFA code is empty");

        ValidMfaChallenge valid = mfaChallengeValidator.validate(mfaToken, MfaChallengePurpose.VERIFICATION);
        UserDomain user = valid.user();
        MfaChallengeDomain challenge = valid.challenge();

        UserTotpDomain totp = userTotpPersistencePort.findByUserId(user.id())
                .filter(UserTotpDomain::isConfirmed)
                .orElseThrow(() -> new BusinessException(MfaChallengeValidator.INVALID_MFA_TOKEN));

        String secret = secretEncryptionPort.decrypt(totp.encryptedSecret());

        long timeStep = totpPort.verifyCode(secret, code, totp.lastUsedTimeStep())
                .orElseThrow(() -> mfaChallengeValidator.registerFailedAttempt(challenge));

        AuthResponse authResponse = transactionManager.execute(() -> {
            userTotpPersistencePort.save(totp.registerUse(timeStep));
            mfaChallengePersistencePort.save(challenge.markAsUsed());
            return issueAuthTokensUseCase.execute(user, ip, device);
        });

        signInNotifier.notifyNewSignIn(user, ip, device);

        return authResponse;
    }
}
