package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.mfa.IssueMfaChallengeUseCase;
import com.auth.api.core.auth.application.port.in.password.ComparePasswordUseCase;
import com.auth.api.core.auth.application.port.in.sign.SignInUseCase;
import com.auth.api.core.auth.application.service.sign.SignInService;
import com.auth.api.core.user.application.port.out.UserAuthenticationPersistencePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignInBeanConfig {

    @Bean
    public SignInUseCase signInUseCaseBean(
            UserAuthenticationPersistencePort userAuthenticationPort,
            ComparePasswordUseCase comparePasswordUseCase,
            IssueMfaChallengeUseCase issueMfaChallengeUseCase
    ) {
        return new SignInService(
                userAuthenticationPort,
                comparePasswordUseCase,
                issueMfaChallengeUseCase
        );
    }
}
