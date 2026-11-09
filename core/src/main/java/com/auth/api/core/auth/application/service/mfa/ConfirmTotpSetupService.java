package com.auth.api.core.auth.application.service.mfa;

import com.auth.api.core.auth.application.dto.response.AuthResponse;
import com.auth.api.core.auth.application.dto.response.MfaSetupCompletedResponse;
import com.auth.api.core.auth.application.dto.response.ValidMfaChallenge;
import com.auth.api.core.auth.application.port.in.mfa.ConfirmTotpSetupUseCase;
import com.auth.api.core.auth.application.port.in.mfa.HashRecoveryCodeUseCase;
import com.auth.api.core.auth.application.port.in.sign.IssueAuthTokensUseCase;
import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.application.port.out.RecoveryCodePersistencePort;
import com.auth.api.core.auth.application.port.out.SecretEncryptionPort;
import com.auth.api.core.auth.application.port.out.TotpPort;
import com.auth.api.core.auth.application.port.out.UserTotpPersistencePort;
import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.core.auth.domain.MfaChallengePurpose;
import com.auth.api.core.auth.domain.RecoveryCodeDomain;
import com.auth.api.core.auth.domain.UserTotpDomain;
import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

import java.util.List;

import static com.auth.api.core.shared.util.ValidationUtils.requireNotBlank;

/**
 * Confirma o vínculo do app autenticador: valida o primeiro código, ativa o MFA,
 * gera os códigos de recuperação e conclui o login.
 */
public class ConfirmTotpSetupService implements ConfirmTotpSetupUseCase {

    public static final int RECOVERY_CODES_AMOUNT = 10;

    private final MfaChallengeValidator mfaChallengeValidator;
    private final MfaChallengePersistencePort mfaChallengePersistencePort;
    private final UserTotpPersistencePort userTotpPersistencePort;
    private final RecoveryCodePersistencePort recoveryCodePersistencePort;
    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;
    private final TotpPort totpPort;
    private final SecretEncryptionPort secretEncryptionPort;
    private final HashRecoveryCodeUseCase hashRecoveryCodeUseCase;
    private final IssueAuthTokensUseCase issueAuthTokensUseCase;
    private final TransactionManager transactionManager;
    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;

    public ConfirmTotpSetupService(
            MfaChallengeValidator mfaChallengeValidator,
            MfaChallengePersistencePort mfaChallengePersistencePort,
            UserTotpPersistencePort userTotpPersistencePort,
            RecoveryCodePersistencePort recoveryCodePersistencePort,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            TotpPort totpPort,
            SecretEncryptionPort secretEncryptionPort,
            HashRecoveryCodeUseCase hashRecoveryCodeUseCase,
            IssueAuthTokensUseCase issueAuthTokensUseCase,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        this.mfaChallengeValidator = mfaChallengeValidator;
        this.mfaChallengePersistencePort = mfaChallengePersistencePort;
        this.userTotpPersistencePort = userTotpPersistencePort;
        this.recoveryCodePersistencePort = recoveryCodePersistencePort;
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
        this.totpPort = totpPort;
        this.secretEncryptionPort = secretEncryptionPort;
        this.hashRecoveryCodeUseCase = hashRecoveryCodeUseCase;
        this.issueAuthTokensUseCase = issueAuthTokensUseCase;
        this.transactionManager = transactionManager;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    @Override
    public MfaSetupCompletedResponse execute(String mfaToken, String code, String ip, String device) {
        requireNotBlank(code, "MFA code is empty");

        ValidMfaChallenge valid = mfaChallengeValidator.validate(mfaToken, MfaChallengePurpose.SETUP);
        UserDomain user = valid.user();
        MfaChallengeDomain challenge = valid.challenge();

        if (user.isMfaEnabled()) {
            throw new BusinessException("MFA is already enabled");
        }

        // um desafio que ainda aguarda o código do e-mail não pode confirmar o
        // setup, mesmo que exista um segredo pendente criado por outro desafio.
        if (challenge.requiresEmailCode()) {
            throw new BusinessException("Email code is required to set up MFA. Call the setup endpoint first");
        }

        UserTotpDomain pendingTotp = userTotpPersistencePort.findByUserId(user.id())
                .filter(totp -> !totp.isConfirmed())
                .orElseThrow(() -> new BusinessException("TOTP setup not started. Call the setup endpoint first"));

        String secret = secretEncryptionPort.decrypt(pendingTotp.encryptedSecret());

        long timeStep = totpPort.verifyCode(secret, code, pendingTotp.lastUsedTimeStep())
                .orElseThrow(() -> mfaChallengeValidator.registerFailedAttempt(challenge));

        List<String> recoveryCodes = totpPort.generateRecoveryCodes(RECOVERY_CODES_AMOUNT);
        List<RecoveryCodeDomain> hashedRecoveryCodes = recoveryCodes.stream()
                .map(rc -> RecoveryCodeDomain.create(user.id(), hashRecoveryCodeUseCase.execute(rc)))
                .toList();

        UserDomain mfaEnabledUser = user.enableMfa();

        AuthResponse authResponse = transactionManager.execute(() -> {
            userTotpPersistencePort.save(pendingTotp.confirm(timeStep));
            userAuthenticationPersistencePort.save(mfaEnabledUser);
            mfaChallengePersistencePort.save(challenge.markAsUsed());
            recoveryCodePersistencePort.deleteByUserId(user.id());
            recoveryCodePersistencePort.saveAll(hashedRecoveryCodes);
            return issueAuthTokensUseCase.execute(mfaEnabledUser, ip, device);
        });

        emailPort.send(
                new EmailMessage(
                        mfaEnabledUser.email(),
                        "Two-factor authentication enabled",
                        emailTemplateUseCase.buildMfaEnabledEmail(mfaEnabledUser.name())
                )
        );

        return new MfaSetupCompletedResponse(authResponse, recoveryCodes);
    }
}
