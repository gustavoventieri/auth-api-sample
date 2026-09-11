package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.core.auth.domain.RefreshToken;
import com.auth.api.framework.auth.adapter.cast.RefreshTokenCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringRefreshTokenDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenPersistencePort {

    private final SpringRefreshTokenDataRepository springRefreshTokenDataRepository;

    public RefreshTokenRepositoryAdapter(SpringRefreshTokenDataRepository springRefreshTokenDataRepository){
        this.springRefreshTokenDataRepository = springRefreshTokenDataRepository;
    }

    @Override
    public void save(RefreshToken refreshToken) {
        springRefreshTokenDataRepository.save(RefreshTokenCast.toEntity(refreshToken));
    }

    @Override
    public Optional<RefreshToken> findRefreshTokenByHashRefreshToken(String refreshToken) {
        return springRefreshTokenDataRepository.findByTokenHash(refreshToken).map(RefreshTokenCast::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByPublicIdAndUserId(String publicId, String userId) {
        return springRefreshTokenDataRepository.findByPublicIdAndUser_Id(publicId, userId).map(RefreshTokenCast::toDomain);
    }
}
