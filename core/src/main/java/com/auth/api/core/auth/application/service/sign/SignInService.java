package com.auth.api.core.auth.application.service.sign;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;
import com.auth.api.core.auth.application.port.in.mfa.IssueMfaChallengeUseCase;
import com.auth.api.core.auth.application.port.in.password.ComparePasswordUseCase;
import com.auth.api.core.auth.application.port.in.sign.SignInUseCase;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.exception.NotFoundException;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

/**
 * Primeira etapa do login. A senha correta NÃO gera sessão: gera um desafio MFA
 * (setup do TOTP ou verificação do código), já que o MFA é obrigatório.
 */
public class SignInService implements SignInUseCase {

    private static final String INVALID_CREDENTIALS = "Email or Password Invalid";

    private final UserAuthenticationPersistencePort userAuthenticationPort;
    private final ComparePasswordUseCase comparePasswordUseCase;
    private final IssueMfaChallengeUseCase issueMfaChallengeUseCase;

    public SignInService(
            UserAuthenticationPersistencePort userAuthenticationPort,
            ComparePasswordUseCase comparePasswordUseCase,
            IssueMfaChallengeUseCase issueMfaChallengeUseCase
    ) {
        this.userAuthenticationPort = userAuthenticationPort;
        this.comparePasswordUseCase = comparePasswordUseCase;
        this.issueMfaChallengeUseCase = issueMfaChallengeUseCase;
    }

    @Override
    public MfaChallengeResponse execute(String email, String password) {

        UserDomain userDomain = userAuthenticationPort.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(INVALID_CREDENTIALS));

        if (!userDomain.isVerified()) throw new BusinessException("Email not verified yet");

        if (!userDomain.isActive()) throw new BusinessException("User not Active");

        if (!comparePasswordUseCase.execute(password, userDomain.password())) {
            throw new NotFoundException(INVALID_CREDENTIALS);
        }

        return issueMfaChallengeUseCase.execute(userDomain);
    }
}
