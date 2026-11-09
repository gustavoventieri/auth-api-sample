package com.auth.api.core.auth.application.port.in.mfa;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;
import com.auth.api.core.user.domain.UserDomain;

public interface IssueMfaChallengeUseCase {
    MfaChallengeResponse execute(UserDomain user);
}
