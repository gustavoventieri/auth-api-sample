package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.core.auth.application.port.out.EmailVerificationTokenPort;
import com.auth.core.auth.domain.EmailVerificationToken;
import com.auth.api.framework.auth.adapter.cast.EmailVerificationTokenCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringEmailVerificationTokenDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EmailVerificationTokenAdapter implements EmailVerificationTokenPort {

    private final SpringEmailVerificationTokenDataRepository springEmailVerificationTokenDataRepository;

    public EmailVerificationTokenAdapter(SpringEmailVerificationTokenDataRepository springEmailVerificationTokenDataRepository){
        this.springEmailVerificationTokenDataRepository = springEmailVerificationTokenDataRepository;
    }


    @Override
    public void save(EmailVerificationToken emailVerificationToken) {
        springEmailVerificationTokenDataRepository.save(EmailVerificationTokenCast.toEntity(emailVerificationToken));
    }

    @Override
    public Optional<EmailVerificationToken> findByUserId(String userId) {
        return springEmailVerificationTokenDataRepository.findByUserId(userId).map(EmailVerificationTokenCast::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        springEmailVerificationTokenDataRepository.deleteByUserId(userId);
    }
}
