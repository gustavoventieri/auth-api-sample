package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.EmailVerificationTokenPort;

import com.auth.api.core.auth.domain.EmailVerificationTokenDomain;
import com.auth.api.framework.auth.adapter.cast.EmailVerificationTokenCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringEmailVerificationTokenDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.SpringUserDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EmailVerificationTokenAdapter implements EmailVerificationTokenPort {

    private final SpringUserDataRepository userRepository;

    private final SpringEmailVerificationTokenDataRepository springEmailVerificationTokenDataRepository;

    public EmailVerificationTokenAdapter(
            SpringEmailVerificationTokenDataRepository springEmailVerificationTokenDataRepository,
            SpringUserDataRepository userRepository
    ) {
        this.userRepository = userRepository;
        this.springEmailVerificationTokenDataRepository = springEmailVerificationTokenDataRepository;
    }


    @Override
    public void save(EmailVerificationTokenDomain emailVerificationToken) {
        springEmailVerificationTokenDataRepository.save(EmailVerificationTokenCast.toEntity(emailVerificationToken, userRef(emailVerificationToken.userId())));
    }

    @Override
    public Optional<EmailVerificationTokenDomain> findByUserId(String userId) {
        return springEmailVerificationTokenDataRepository.findByUserId(userId).map(EmailVerificationTokenCast::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        springEmailVerificationTokenDataRepository.deleteByUserId(userId);
    }

    /** Proxy gerenciado do usuário para a FK (não faz SELECT e respeita o @Version do UserEntity). */
    private UserEntity userRef(String userId) {
        return userRepository.getReferenceById(userId);
    }
}
