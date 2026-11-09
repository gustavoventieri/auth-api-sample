package com.auth.api.core.auth.application.service.emailVerification;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;
import com.auth.api.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.emailVerification.VerifyEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.IssueMfaChallengeUseCase;
import com.auth.api.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.api.core.auth.domain.EmailVerificationTokenDomain;
import com.auth.api.core.shared.email.EmailMessage;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.exception.BusinessException;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.domain.UserDomain;

import static com.auth.api.core.shared.util.SecurityUtils.constantTimeEquals;

public class VerifyEmailVerificationTokenService implements VerifyEmailVerificationTokenUseCase {

    private static final String INVALID_CODE = "Invalid verification code";

    private final UserAuthenticationPersistencePort userAuthenticationPort;
    private final EmailVerificationTokenPort emailVerificationTokenPort;
    private final HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase;
    private final IssueMfaChallengeUseCase issueMfaChallengeUseCase;
    private final TransactionManager transactionManager;
    private final EmailPort emailPort;
    private final EmailTemplateUseCase emailTemplateUseCase;

    public VerifyEmailVerificationTokenService(
            UserAuthenticationPersistencePort userAuthenticationPort,
            EmailVerificationTokenPort emailVerificationTokenPort,
            HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase,
            IssueMfaChallengeUseCase issueMfaChallengeUseCase,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        this.userAuthenticationPort = userAuthenticationPort;
        this.emailVerificationTokenPort = emailVerificationTokenPort;
        this.hashEmailVerificationTokenUseCase = hashEmailVerificationTokenUseCase;
        this.issueMfaChallengeUseCase = issueMfaChallengeUseCase;
        this.transactionManager = transactionManager;
        this.emailPort = emailPort;
        this.emailTemplateUseCase = emailTemplateUseCase;
    }

    @Override
    public MfaChallengeResponse execute(String otpCode, String email) {

        UserDomain userDomain = userAuthenticationPort.findByEmail(email)
                .orElseThrow(() -> new BusinessException(INVALID_CODE));

        EmailVerificationTokenDomain token = emailVerificationTokenPort.findByUserId(userDomain.id())
                .orElseThrow(() -> new BusinessException(INVALID_CODE));

        if (token.isUsed()) {
            throw new BusinessException("Verification code already used");
        }

        if (token.isExpired()) {
            throw new BusinessException("Verification code expired");
        }

        if (!constantTimeEquals(hashEmailVerificationTokenUseCase.execute(otpCode), token.otpCode())) {
            throw new BusinessException(INVALID_CODE);
        }

        UserDomain verifiedUser = userDomain.verify();
        EmailVerificationTokenDomain usedToken = token.markAsUsed();

        // verificar o e-mail não cria sessão, apenas libera o setup do TOTP.
        MfaChallengeResponse response = transactionManager.execute(() -> {
            userAuthenticationPort.save(verifiedUser);
            emailVerificationTokenPort.save(usedToken);
            // O código do e-mail acabou de ser validado: o setup do TOTP pode seguir direto.
            return issueMfaChallengeUseCase.execute(verifiedUser, true);
        });

        emailPort.send(
                new EmailMessage(
                        verifiedUser.email(),
                        "Your account was created",
                        emailTemplateUseCase.buildAccountCreatedEmail(verifiedUser.name(), otpCode)
                )
        );

        return response;
    }
}
