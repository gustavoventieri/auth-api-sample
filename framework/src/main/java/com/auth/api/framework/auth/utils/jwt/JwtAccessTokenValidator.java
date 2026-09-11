package com.auth.api.framework.auth.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth.core.auth.application.port.in.accessToken.ValidateAccessTokenUseCase;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessTokenValidator implements ValidateAccessTokenUseCase {

    private final JWTVerifier verifier;

    public JwtAccessTokenValidator(JwtProperties properties) {

        Algorithm algorithm = Algorithm.HMAC256(properties.secret());

        this.verifier = JWT
                .require(algorithm)
                .withIssuer(properties.issuer())
                .build();
    }

    @Override
    public boolean execute(String token) {
        if (token == null) return false;

        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }

    }

}