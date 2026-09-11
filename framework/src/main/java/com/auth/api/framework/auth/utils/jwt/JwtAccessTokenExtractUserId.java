package com.auth.api.framework.auth.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth.core.auth.application.port.in.accessToken.ExtractUserIdFromAccessTokenUseCase;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessTokenExtractUserId
        implements ExtractUserIdFromAccessTokenUseCase {

    private final JWTVerifier verifier;

    public JwtAccessTokenExtractUserId(JwtProperties properties) {

        Algorithm algorithm = Algorithm.HMAC256(properties.secret());

        this.verifier = JWT
                .require(algorithm)
                .withIssuer(properties.issuer())
                .build();
    }

    @Override
    public String execute(String token) {

        DecodedJWT jwt = verifier.verify(token);

        return jwt.getSubject();
    }

}