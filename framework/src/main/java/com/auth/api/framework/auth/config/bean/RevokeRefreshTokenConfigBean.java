package com.auth.api.framework.auth.config.bean;

import com.auth.api.core.auth.application.port.in.refreshToken.RevokeRefreshTokenUseCase;
import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.application.service.RevokeRefreshTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RevokeRefreshTokenConfigBean {
    @Bean
    public RevokeRefreshTokenUseCase revokeRefreshTokenUseCaseConfigBean(
            RefreshTokenPersistencePort refreshTokenRepositoryPort
    ){
        return new RevokeRefreshTokenService(
                refreshTokenRepositoryPort
        );
    }

}
