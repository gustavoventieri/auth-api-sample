package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.emailVerification.VerifyEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.mfa.IssueMfaChallengeUseCase;
import com.auth.api.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.api.core.auth.application.service.emailVerification.VerifyEmailVerificationTokenService;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VerifyEmailVerificationTokenConfig {

    @Bean
    public VerifyEmailVerificationTokenUseCase verifyEmailVerificationTokenUseCase(
            UserAuthenticationPersistencePort userAuthenticationPort,
            EmailVerificationTokenPort emailVerificationTokenPort,
            HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase,
            IssueMfaChallengeUseCase issueMfaChallengeUseCase,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        return new VerifyEmailVerificationTokenService(
                userAuthenticationPort,
                emailVerificationTokenPort,
                hashEmailVerificationTokenUseCase,
                issueMfaChallengeUseCase,
                transactionManager,
                emailPort,
                emailTemplateUseCase
        );
    }
}
