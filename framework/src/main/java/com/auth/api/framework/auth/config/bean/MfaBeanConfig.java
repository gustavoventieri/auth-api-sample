package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.mfa.AdminResetMfaUseCase;
import com.auth.api.core.auth.application.port.in.mfa.ConfirmTotpSetupUseCase;
import com.auth.api.core.auth.application.port.in.mfa.GenerateMfaChallengeTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.HashMfaChallengeTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.HashRecoveryCodeUseCase;
import com.auth.api.core.auth.application.port.in.mfa.IssueMfaChallengeUseCase;
import com.auth.api.core.auth.application.port.in.mfa.SetupTotpUseCase;
import com.auth.api.core.auth.application.port.in.mfa.VerifyRecoveryCodeUseCase;
import com.auth.api.core.auth.application.port.in.mfa.VerifyTotpUseCase;
import com.auth.api.core.auth.application.port.in.sign.IssueAuthTokensUseCase;
import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.application.port.out.RecoveryCodePersistencePort;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.application.port.out.SecretEncryptionPort;
import com.auth.api.core.auth.application.port.out.TotpPort;
import com.auth.api.core.auth.application.port.out.UserTotpPersistencePort;
import com.auth.api.core.auth.application.service.mfa.AdminResetMfaService;
import com.auth.api.core.auth.application.service.mfa.ConfirmTotpSetupService;
import com.auth.api.core.auth.application.service.mfa.IssueMfaChallengeService;
import com.auth.api.core.auth.application.service.mfa.MfaChallengeValidator;
import com.auth.api.core.auth.application.service.mfa.SetupTotpService;
import com.auth.api.core.auth.application.service.sign.SignInNotifier;
import com.auth.api.core.auth.application.service.mfa.VerifyRecoveryCodeService;
import com.auth.api.core.auth.application.service.mfa.VerifyTotpService;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MfaBeanConfig {

    @Bean
    public SignInNotifier signInNotifier(
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        return new SignInNotifier(emailPort, emailTemplateUseCase);
    }

    @Bean
    public MfaChallengeValidator mfaChallengeValidator(
            MfaChallengePersistencePort mfaChallengePersistencePort,
            HashMfaChallengeTokenUseCase hashMfaChallengeTokenUseCase,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort
    ) {
        return new MfaChallengeValidator(
                mfaChallengePersistencePort,
                hashMfaChallengeTokenUseCase,
                userAuthenticationPersistencePort
        );
    }

    @Bean
    public IssueMfaChallengeUseCase issueMfaChallengeUseCase(
            MfaChallengePersistencePort mfaChallengePersistencePort,
            GenerateMfaChallengeTokenUseCase generateMfaChallengeTokenUseCase,
            HashMfaChallengeTokenUseCase hashMfaChallengeTokenUseCase,
            TransactionManager transactionManager
    ) {
        return new IssueMfaChallengeService(
                mfaChallengePersistencePort,
                generateMfaChallengeTokenUseCase,
                hashMfaChallengeTokenUseCase,
                transactionManager
        );
    }

    @Bean
    public SetupTotpUseCase setupTotpUseCase(
            MfaChallengeValidator mfaChallengeValidator,
            UserTotpPersistencePort userTotpPersistencePort,
            TotpPort totpPort,
            SecretEncryptionPort secretEncryptionPort,
            TransactionManager transactionManager
    ) {
        return new SetupTotpService(
                mfaChallengeValidator,
                userTotpPersistencePort,
                totpPort,
                secretEncryptionPort,
                transactionManager
        );
    }

    @Bean
    public ConfirmTotpSetupUseCase confirmTotpSetupUseCase(
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
        return new ConfirmTotpSetupService(
                mfaChallengeValidator,
                mfaChallengePersistencePort,
                userTotpPersistencePort,
                recoveryCodePersistencePort,
                userAuthenticationPersistencePort,
                totpPort,
                secretEncryptionPort,
                hashRecoveryCodeUseCase,
                issueAuthTokensUseCase,
                transactionManager,
                emailPort,
                emailTemplateUseCase
        );
    }

    @Bean
    public VerifyTotpUseCase verifyTotpUseCase(
            MfaChallengeValidator mfaChallengeValidator,
            MfaChallengePersistencePort mfaChallengePersistencePort,
            UserTotpPersistencePort userTotpPersistencePort,
            TotpPort totpPort,
            SecretEncryptionPort secretEncryptionPort,
            IssueAuthTokensUseCase issueAuthTokensUseCase,
            TransactionManager transactionManager,
            SignInNotifier signInNotifier
    ) {
        return new VerifyTotpService(
                mfaChallengeValidator,
                mfaChallengePersistencePort,
                userTotpPersistencePort,
                totpPort,
                secretEncryptionPort,
                issueAuthTokensUseCase,
                transactionManager,
                signInNotifier
        );
    }

    @Bean
    public VerifyRecoveryCodeUseCase verifyRecoveryCodeUseCase(
            MfaChallengeValidator mfaChallengeValidator,
            MfaChallengePersistencePort mfaChallengePersistencePort,
            RecoveryCodePersistencePort recoveryCodePersistencePort,
            HashRecoveryCodeUseCase hashRecoveryCodeUseCase,
            IssueAuthTokensUseCase issueAuthTokensUseCase,
            TransactionManager transactionManager,
            SignInNotifier signInNotifier
    ) {
        return new VerifyRecoveryCodeService(
                mfaChallengeValidator,
                mfaChallengePersistencePort,
                recoveryCodePersistencePort,
                hashRecoveryCodeUseCase,
                issueAuthTokensUseCase,
                transactionManager,
                signInNotifier
        );
    }

    @Bean
    public AdminResetMfaUseCase adminResetMfaUseCase(
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            UserTotpPersistencePort userTotpPersistencePort,
            RecoveryCodePersistencePort recoveryCodePersistencePort,
            MfaChallengePersistencePort mfaChallengePersistencePort,
            RefreshTokenPersistencePort refreshTokenPersistencePort,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        return new AdminResetMfaService(
                userAuthenticationPersistencePort,
                userTotpPersistencePort,
                recoveryCodePersistencePort,
                mfaChallengePersistencePort,
                refreshTokenPersistencePort,
                transactionManager,
                emailPort,
                emailTemplateUseCase
        );
    }
}
