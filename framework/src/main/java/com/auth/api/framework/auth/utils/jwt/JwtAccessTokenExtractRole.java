package com.auth.api.framework.auth.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth.core.auth.application.port.in.accessToken.ExtractRoleFromAccessTokenUseCase;
import com.auth.core.shared.enumerated.Roles;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessTokenExtractRole
        implements ExtractRoleFromAccessTokenUseCase {

    private final JWTVerifier verifier;

    public JwtAccessTokenExtractRole(JwtProperties properties) {

        Algorithm algorithm = Algorithm.HMAC256(properties.secret());

        this.verifier = JWT
                .require(algorithm)
                .withIssuer(properties.issuer())
                .build();
    }

    @Override
    public Roles execute(String token) {

        DecodedJWT jwt = verifier.verify(token);

        String role = jwt
                .getClaim("role")
                .asString();

        if (role == null) {
            throw new IllegalArgumentException(
                    "Role not found in access token"
            );
        }

        return Roles.valueOf(role);
    }
}