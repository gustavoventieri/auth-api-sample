package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.password.ComparePasswordUseCase;
import com.auth.api.core.auth.application.port.in.sign.IssueAuthTokensUseCase;
import com.auth.api.core.auth.application.port.in.sign.SignInUseCase;
import com.auth.api.core.auth.application.service.SignInService;
import com.auth.api.core.shared.email.EmailPort;
import com.auth.api.core.shared.email.EmailTemplateUseCase;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignInBeanConfig {

    @Bean
    public SignInUseCase signInUseCaseBean(
            UserAuthenticationPersistencePort userAuthenticationPort,
            ComparePasswordUseCase comparePasswordUseCase,
            IssueAuthTokensUseCase issueAuthTokensUseCase,
            EmailPort emailPort,
            EmailTemplateUseCase emailTemplateUseCase
    ) {
        return new SignInService(
                userAuthenticationPort,
                comparePasswordUseCase,
                issueAuthTokensUseCase,
                emailPort,
                emailTemplateUseCase
        );
    }
}
