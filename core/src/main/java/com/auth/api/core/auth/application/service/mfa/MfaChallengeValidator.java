package com.auth.api.core.auth.application.service.mfa;

import com.auth.api.core.auth.application.dto.response.ValidMfaChallenge;
import com.auth.api.core.auth.application.port.in.mfa.HashMfaChallengeTokenUseCase;
import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.core.auth.domain.MfaChallengePurpose;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

import static com.auth.api.core.shared.util.ValidationUtils.requireNotBlank;

/**
 * Valida o mfaToken recebido do cliente e registra tentativas com código errado.
 */
public class MfaChallengeValidator {

    static final String INVALID_MFA_TOKEN = "MFA token is invalid";
    static final String INVALID_MFA_CODE = "Invalid MFA code";

    private final MfaChallengePersistencePort mfaChallengePersistencePort;
    private final HashMfaChallengeTokenUseCase hashMfaChallengeTokenUseCase;
    private final UserAuthenticationPersistencePort userAuthenticationPersistencePort;

    public MfaChallengeValidator(
            MfaChallengePersistencePort mfaChallengePersistencePort,
            HashMfaChallengeTokenUseCase hashMfaChallengeTokenUseCase,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort
    ) {
        this.mfaChallengePersistencePort = mfaChallengePersistencePort;
        this.hashMfaChallengeTokenUseCase = hashMfaChallengeTokenUseCase;
        this.userAuthenticationPersistencePort = userAuthenticationPersistencePort;
    }

    public ValidMfaChallenge validate(String rawToken, MfaChallengePurpose expectedPurpose) {
        requireNotBlank(rawToken, "MFA token is empty");

        MfaChallengeDomain challenge = mfaChallengePersistencePort
                .findByTokenHash(hashMfaChallengeTokenUseCase.execute(rawToken))
                .filter(c -> c.purpose() == expectedPurpose)
                .orElseThrow(() -> new BusinessException(INVALID_MFA_TOKEN));

        if (!challenge.isUsable()) {
            throw new BusinessException("MFA token is expired, was already used or has too many failed attempts. Sign in again");
        }

        UserDomain user = userAuthenticationPersistencePort.findById(challenge.userId())
                .filter(UserDomain::isActiveAndVerified)
                .orElseThrow(() -> new BusinessException(INVALID_MFA_TOKEN));

        return new ValidMfaChallenge(user, challenge);
    }

    /**
     * Incrementa o contador de falhas do desafio e lança a exceção de código inválido.
     * Ao atingir o limite, o desafio deixa de ser utilizável e o usuário precisa logar de novo.
     */
    public BusinessException registerFailedAttempt(MfaChallengeDomain challenge) {
        MfaChallengeDomain updated = challenge.registerFailedAttempt();
        mfaChallengePersistencePort.save(updated);

        if (!updated.hasAttemptsLeft()) {
            return new BusinessException("Too many invalid MFA codes. Sign in again");
        }

        return new BusinessException(INVALID_MFA_CODE);
    }
}
