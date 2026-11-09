package com.auth.api.framework.auth.adapter.port.out.persistence;

import com.auth.api.core.auth.application.port.out.MfaChallengePersistencePort;
import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.framework.auth.adapter.cast.MfaChallengeCast;
import com.auth.api.framework.auth.adapter.port.out.persistence.repository.SpringMfaChallengeDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MfaChallengeAdapter implements MfaChallengePersistencePort {

    private final SpringMfaChallengeDataRepository repository;

    public MfaChallengeAdapter(SpringMfaChallengeDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(MfaChallengeDomain challenge) {
        repository.save(MfaChallengeCast.toEntity(challenge));
    }

    @Override
    public Optional<MfaChallengeDomain> findByTokenHash(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(MfaChallengeCast::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }
}
