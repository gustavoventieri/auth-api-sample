package com.auth.api.core.auth.application.dto.response;

import com.auth.api.core.auth.domain.MfaChallengeDomain;
import com.auth.api.core.user.domain.UserDomain;

public record ValidMfaChallenge(
        UserDomain user,
        MfaChallengeDomain challenge
) {
}
