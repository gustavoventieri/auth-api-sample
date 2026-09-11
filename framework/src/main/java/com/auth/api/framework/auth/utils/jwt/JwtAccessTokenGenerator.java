package com.auth.api.framework.auth.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth.core.auth.application.port.in.accessToken.GenerateAccessTokenUseCase;
import com.auth.core.shared.enumerated.Roles;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtAccessTokenGenerator implements GenerateAccessTokenUseCase {

    private final JwtProperties properties;
    private final Algorithm algorithm;

    public JwtAccessTokenGenerator(JwtProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(properties.secret());
    }

    @Override
    public String execute(String userId, Roles role) {

        Instant now = Instant.now();

        return JWT.create()
                .withIssuer(properties.issuer())
                .withSubject(userId)
                .withClaim("role", role.name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(properties.expiration())))
                .sign(algorithm);
    }
}
