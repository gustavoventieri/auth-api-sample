package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.RefreshTokenPersistencePort;
import com.auth.api.core.auth.domain.RefreshTokenDomain;
import com.auth.api.framework.auth.adapter.cast.RefreshTokenCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringRefreshTokenDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.SpringUserDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenPersistencePort {

    private final SpringUserDataRepository userRepository;

    private final SpringRefreshTokenDataRepository springRefreshTokenDataRepository;

    public RefreshTokenRepositoryAdapter(
            SpringRefreshTokenDataRepository springRefreshTokenDataRepository,
            SpringUserDataRepository userRepository
    ) {
        this.userRepository = userRepository;
        this.springRefreshTokenDataRepository = springRefreshTokenDataRepository;
    }

    @Override
    public void save(RefreshTokenDomain refreshToken) {
        springRefreshTokenDataRepository.save(RefreshTokenCast.toEntity(refreshToken, userRef(refreshToken.userId())));
    }

    @Override
    public Optional<RefreshTokenDomain> findRefreshTokenByHashRefreshToken(String refreshToken) {
        return springRefreshTokenDataRepository.findByTokenHash(refreshToken).map(RefreshTokenCast::toDomain);
    }

    @Override
    public int revokeAllActiveByUserId(String userId) {
        return springRefreshTokenDataRepository.revokeAllActiveByUserId(userId, LocalDateTime.now());
    }

    @Override
    public Optional<RefreshTokenDomain> findByPublicIdAndUserId(String publicId, String userId) {
        return springRefreshTokenDataRepository.findByPublicIdAndUser_Id(publicId, userId).map(RefreshTokenCast::toDomain);
    }

    /** Proxy gerenciado do usuário para a FK (não faz SELECT e respeita o @Version do UserEntity). */
    private UserEntity userRef(String userId) {
        return userRepository.getReferenceById(userId);
    }
}
