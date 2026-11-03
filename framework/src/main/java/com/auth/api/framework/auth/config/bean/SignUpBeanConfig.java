package com.auth.api.framework.auth.config.bean;


import com.auth.api.core.auth.application.port.in.emailVerification.GenerateEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.emailVerification.HashEmailVerificationTokenUseCase;
import com.auth.api.core.auth.application.port.in.password.HashPasswordUseCase;
import com.auth.api.core.auth.application.port.in.sign.SignUpUseCase;
import com.auth.api.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.api.core.auth.application.service.SignUpService;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.shared.transaction.TransactionManager;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import com.auth.api.core.user.application.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignUpBeanConfig {

    @Bean
    public SignUpUseCase signUpUseCaseBean(
            UserAuthenticationPersistencePort userAuthenticationPort,
            UserRepositoryPort userRepositoryPort,
            EmailVerificationTokenPort emailVerificationTokenRepositoryPort,
            HashPasswordUseCase hashPasswordUseCase,
            GenerateEmailVerificationTokenUseCase generateEmailVerificationTokenUseCase,
            HashEmailVerificationTokenUseCase hashEmailVerificationTokenUseCase,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase,
            TransactionManager transactionManager
    ){
        return new SignUpService(
                 userAuthenticationPort,
                 userRepositoryPort,
                 emailVerificationTokenRepositoryPort,
                 hashPasswordUseCase,
                 generateEmailVerificationTokenUseCase,
                 hashEmailVerificationTokenUseCase,
                 emailPort,
                 emailTemplateUseCase,
                 transactionManager
        );
    }
}
