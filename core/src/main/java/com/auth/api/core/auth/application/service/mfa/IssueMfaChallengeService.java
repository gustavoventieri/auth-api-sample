package com.auth.api.core.auth.application.service.mfa;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;
import com.auth.api.core.auth.application.port.in.mfa.GenerateMfaChallengeTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.HashMfaChallengeTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.IssueMfaChallengeUseCase;
import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.core.auth.domain.MfaChallengePurpose;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.domain.UserDomain;

/**
 * Cria o desafio MFA que substitui a emissão direta de tokens após a senha.
 * Usuários com MFA PENDING recebem um desafio de SETUP; com MFA ENABLED, de VERIFICATION.
 * Só existe um desafio ativo por usuário: os anteriores são apagados.
 */
public class IssueMfaChallengeService implements IssueMfaChallengeUseCase {

    private final MfaChallengePersistencePort mfaChallengePersistencePort;
    private final GenerateMfaChallengeTokenUseCase generateMfaChallengeTokenUseCase;
    private final HashMfaChallengeTokenUseCase hashMfaChallengeTokenUseCase;
    private final TransactionManager transactionManager;

    public IssueMfaChallengeService(
            MfaChallengePersistencePort mfaChallengePersistencePort,
            GenerateMfaChallengeTokenUseCase generateMfaChallengeTokenUseCase,
            HashMfaChallengeTokenUseCase hashMfaChallengeTokenUseCase,
            TransactionManager transactionManager
    ) {
        this.mfaChallengePersistencePort = mfaChallengePersistencePort;
        this.generateMfaChallengeTokenUseCase = generateMfaChallengeTokenUseCase;
        this.hashMfaChallengeTokenUseCase = hashMfaChallengeTokenUseCase;
        this.transactionManager = transactionManager;
    }

    @Override
    public MfaChallengeResponse execute(UserDomain user) {
        MfaChallengePurpose purpose = user.isMfaEnabled()
                ? MfaChallengePurpose.VERIFICATION
                : MfaChallengePurpose.SETUP;

        String rawToken = generateMfaChallengeTokenUseCase.execute();

        MfaChallengeDomain challenge = MfaChallengeDomain.create(
                user.id(),
                hashMfaChallengeTokenUseCase.execute(rawToken),
                purpose
        );

        transactionManager.run(() -> {
            mfaChallengePersistencePort.deleteByUserId(user.id());
            mfaChallengePersistencePort.save(challenge);
        });

        return new MfaChallengeResponse(purpose, rawToken, challenge.expiresAt());
    }
}
