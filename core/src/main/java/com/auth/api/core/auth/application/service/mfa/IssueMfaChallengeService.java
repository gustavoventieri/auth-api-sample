package com.auth.api.core.auth.application.service.mfa;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;
import com.auth.api.core.auth.application.port.in.mfa.GenerateMfaChallengeTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.GenerateMfaEmailCodeUseCase;
import com.auth.api.core.auth.application.port.in.mfa.HashMfaChallengeTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.HashMfaEmailCodeUseCase;
import com.auth.api.core.auth.application.port.in.mfa.IssueMfaChallengeUseCase;
import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.core.auth.domain.MfaChallengePurpose;
import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.domain.UserDomain;

/**
 * Cria o desafio MFA que substitui a emissão direta de tokens após a senha.
 * Usuários com MFA PENDING recebem um desafio de SETUP; com MFA ENABLED, de VERIFICATION.
 * Só existe um desafio ativo por usuário: os anteriores são apagados.
 *
 * Setup vindo só da senha (sign-in de usuário PENDING: contas antigas ou após reset do admin)
 * exige também um código enviado por e-mail. Sem isso, quem tivesse a senha vazada poderia
 * vincular o próprio autenticador antes do dono da conta.
 */
public class IssueMfaChallengeService implements IssueMfaChallengeUseCase {

    private final MfaChallengePersistencePort mfaChallengePersistencePort;
    private final GenerateMfaChallengeTokenUseCase generateMfaChallengeTokenUseCase;
    private final HashMfaChallengeTokenUseCase hashMfaChallengeTokenUseCase;
    private final GenerateMfaEmailCodeUseCase generateMfaEmailCodeUseCase;
    private final HashMfaEmailCodeUseCase hashMfaEmailCodeUseCase;
    private final TransactionManager transactionManager;
    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;

    public IssueMfaChallengeService(
            MfaChallengePersistencePort mfaChallengePersistencePort,
            GenerateMfaChallengeTokenUseCase generateMfaChallengeTokenUseCase,
            HashMfaChallengeTokenUseCase hashMfaChallengeTokenUseCase,
            GenerateMfaEmailCodeUseCase generateMfaEmailCodeUseCase,
            HashMfaEmailCodeUseCase hashMfaEmailCodeUseCase,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        this.mfaChallengePersistencePort = mfaChallengePersistencePort;
        this.generateMfaChallengeTokenUseCase = generateMfaChallengeTokenUseCase;
        this.hashMfaChallengeTokenUseCase = hashMfaChallengeTokenUseCase;
        this.generateMfaEmailCodeUseCase = generateMfaEmailCodeUseCase;
        this.hashMfaEmailCodeUseCase = hashMfaEmailCodeUseCase;
        this.transactionManager = transactionManager;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    @Override
    public MfaChallengeResponse execute(UserDomain user, boolean emailOwnershipProven) {
        MfaChallengePurpose purpose = user.isMfaEnabled()
                ? MfaChallengePurpose.VERIFICATION
                : MfaChallengePurpose.SETUP;

        String rawToken = generateMfaChallengeTokenUseCase.execute();

        MfaChallengeDomain challenge = MfaChallengeDomain.create(
                user.id(),
                hashMfaChallengeTokenUseCase.execute(rawToken),
                purpose
        );

        boolean emailCodeRequired = purpose == MfaChallengePurpose.SETUP && !emailOwnershipProven;
        String emailCode = null;

        if (emailCodeRequired) {
            emailCode = generateMfaEmailCodeUseCase.execute();
            challenge = challenge.withEmailCode(hashMfaEmailCodeUseCase.execute(emailCode));
        }

        MfaChallengeDomain toSave = challenge;
        transactionManager.run(() -> {
            mfaChallengePersistencePort.deleteByUserId(user.id());
            mfaChallengePersistencePort.save(toSave);
        });

        if (emailCodeRequired) {
            emailPort.send(
                    new EmailMessage(
                            user.email(),
                            "Confirm it's you to set up two-factor authentication",
                            emailTemplateUseCase.buildMfaSetupCodeEmail(user.name(), emailCode)
                    )
            );
        }

        return new MfaChallengeResponse(purpose, rawToken, challenge.expiresAt(), emailCodeRequired);
    }
}
