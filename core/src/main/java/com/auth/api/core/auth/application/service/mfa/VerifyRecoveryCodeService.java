package com.auth.api.core.auth.application.service.mfa;

import com.auth.api.core.auth.application.service.sign.SignInNotifier;
import com.auth.api.core.auth.application.dto.response.AuthResponse;
import com.auth.api.core.auth.application.dto.response.ValidMfaChallenge;
import com.auth.api.core.auth.application.port.in.mfa.HashRecoveryCodeUseCase;
import com.auth.api.core.auth.application.port.in.mfa.VerifyRecoveryCodeUseCase;
import com.auth.api.core.auth.application.port.in.sign.IssueAuthTokensUseCase;
import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.application.port.out.RecoveryCodePersistencePort;
import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.core.auth.domain.MfaChallengePurpose;
import com.auth.api.core.auth.domain.RecoveryCodeDomain;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.domain.UserDomain;

import java.util.Locale;

import static com.auth.api.core.shared.util.SecurityUtils.constantTimeEquals;
import static com.auth.api.core.shared.util.ValidationUtils.requireNotBlank;

/**
 * Alternativa ao código TOTP quando o usuário perdeu o autenticador.
 * Cada código de recuperação só pode ser usado uma vez.
 */
public class VerifyRecoveryCodeService implements VerifyRecoveryCodeUseCase {

    private final MfaChallengeValidator mfaChallengeValidator;
    private final MfaChallengePersistencePort mfaChallengePersistencePort;
    private final RecoveryCodePersistencePort recoveryCodePersistencePort;
    private final HashRecoveryCodeUseCase hashRecoveryCodeUseCase;
    private final IssueAuthTokensUseCase issueAuthTokensUseCase;
    private final TransactionManager transactionManager;
    private final SignInNotifier signInNotifier;

    public VerifyRecoveryCodeService(
            MfaChallengeValidator mfaChallengeValidator,
            MfaChallengePersistencePort mfaChallengePersistencePort,
            RecoveryCodePersistencePort recoveryCodePersistencePort,
            HashRecoveryCodeUseCase hashRecoveryCodeUseCase,
            IssueAuthTokensUseCase issueAuthTokensUseCase,
            TransactionManager transactionManager,
            SignInNotifier signInNotifier
    ) {
        this.mfaChallengeValidator = mfaChallengeValidator;
        this.mfaChallengePersistencePort = mfaChallengePersistencePort;
        this.recoveryCodePersistencePort = recoveryCodePersistencePort;
        this.hashRecoveryCodeUseCase = hashRecoveryCodeUseCase;
        this.issueAuthTokensUseCase = issueAuthTokensUseCase;
        this.transactionManager = transactionManager;
        this.signInNotifier = signInNotifier;
    }

    @Override
    public AuthResponse execute(String mfaToken, String recoveryCode, String ip, String device) {
        requireNotBlank(recoveryCode, "Recovery code is empty");

        ValidMfaChallenge valid = mfaChallengeValidator.validate(mfaToken, MfaChallengePurpose.VERIFICATION);
        UserDomain user = valid.user();
        MfaChallengeDomain challenge = valid.challenge();

        String codeHash = hashRecoveryCodeUseCase.execute(normalize(recoveryCode));

        RecoveryCodeDomain matched = recoveryCodePersistencePort.findUnusedByUserId(user.id())
                .stream()
                .filter(rc -> constantTimeEquals(rc.codeHash(), codeHash))
                .findFirst()
                .orElseThrow(() -> mfaChallengeValidator.registerFailedAttempt(challenge));

        AuthResponse authResponse = transactionManager.execute(() -> {
            recoveryCodePersistencePort.save(matched.markAsUsed());
            mfaChallengePersistencePort.save(challenge.markAsUsed());
            return issueAuthTokensUseCase.execute(user, ip, device);
        });

        signInNotifier.notifyNewSignIn(user, ip, device);

        return authResponse;
    }

    private static String normalize(String recoveryCode) {
        return recoveryCode.trim().toLowerCase(Locale.ROOT);
    }
}
