package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.password.HashPasswordUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.GenerateResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.HashResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.SendResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.UpdateResetPasswordUseCase;
import com.auth.api.core.auth.application.port.in.password.reset.VerifyResetPasswordVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.out.ResetPasswordVerificationTokenPersistencePort;
import com.auth.api.core.auth.application.service.password.reset.ResetPasswordTokenValidator;
import com.auth.api.core.auth.application.service.password.reset.SendResetPasswordVerificationTokenService;
import com.auth.api.core.auth.application.service.password.reset.UpdateResetPasswordService;
import com.auth.api.core.auth.application.service.password.reset.VerifyResetPasswordVerificationTokenService;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResetPasswordBeanConfig {

    @Bean
    public ResetPasswordTokenValidator resetPasswordTokenValidator(
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            ResetPasswordVerificationTokenPersistencePort tokenPersistencePort,
            HashResetPasswordVerificationTokenUseCase hashTokenUseCase
    ) {
        return new ResetPasswordTokenValidator(
                userAuthenticationPersistencePort,
                tokenPersistencePort,
                hashTokenUseCase
        );
    }

    @Bean
    public SendResetPasswordVerificationTokenUseCase sendResetPasswordVerificationTokenUseCase(
            ResetPasswordVerificationTokenPersistencePort tokenPersistencePort,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            TransactionManager transactionManager,
            GenerateResetPasswordVerificationTokenUseCase generateTokenUseCase,
            HashResetPasswordVerificationTokenUseCase hashTokenUseCase,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        return new SendResetPasswordVerificationTokenService(
                tokenPersistencePort,
                userAuthenticationPersistencePort,
                transactionManager,
                generateTokenUseCase,
                hashTokenUseCase,
                emailPort,
                emailTemplateUseCase
        );
    }

    @Bean
    public VerifyResetPasswordVerificationTokenUseCase verifyResetPasswordVerificationTokenUseCase(
            ResetPasswordTokenValidator resetPasswordTokenValidator
    ) {
        return new VerifyResetPasswordVerificationTokenService(resetPasswordTokenValidator);
    }

    @Bean
    public UpdateResetPasswordUseCase updateResetPasswordUseCase(
            ResetPasswordTokenValidator resetPasswordTokenValidator,
            UserAuthenticationPersistencePort userAuthenticationPersistencePort,
            ResetPasswordVerificationTokenPersistencePort tokenPersistencePort,
            HashPasswordUseCase hashPasswordUseCase,
            TransactionManager transactionManager,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        return new UpdateResetPasswordService(
                resetPasswordTokenValidator,
                userAuthenticationPersistencePort,
                tokenPersistencePort,
                hashPasswordUseCase,
                transactionManager,
                emailPort,
                emailTemplateUseCase
        );
    }
}
