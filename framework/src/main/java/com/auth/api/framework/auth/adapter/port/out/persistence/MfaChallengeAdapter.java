package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.framework.auth.adapter.cast.MfaChallengeCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringMfaChallengeDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.SpringUserDataRepository;
import com.auth.api.framework.user.adapter.port.out.persistence.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MfaChallengeAdapter implements MfaChallengePersistencePort {

    private final SpringUserDataRepository userRepository;

    private final SpringMfaChallengeDataRepository repository;

    public MfaChallengeAdapter(
            SpringMfaChallengeDataRepository repository,
            SpringUserDataRepository userRepository
    ) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @Override
    public void save(MfaChallengeDomain challenge) {
        repository.save(MfaChallengeCast.toEntity(challenge, userRef(challenge.userId())));
    }

    @Override
    public Optional<MfaChallengeDomain> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(MfaChallengeCast::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }

    /** Proxy gerenciado do usuário para a FK (não faz SELECT e respeita o @Version do UserEntity). */
    private UserEntity userRef(String userId) {
        return userRepository.getReferenceById(userId);
    }
}
