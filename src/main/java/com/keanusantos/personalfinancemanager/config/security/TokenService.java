package com.keanusantos.personalfinancemanager.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.keanusantos.personalfinancemanager.domain.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private String secret = "secret";

    public String generateToken(UserDetailsImpl user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("financemanager-api")
                    .withSubject(user.getUsername())
                    .withExpiresAt(Instant.now().plusSeconds(3600))
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception){
            throw new RuntimeException("error while generating token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("financemanager-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTCreationException exception){
            throw new RuntimeException("error while generating token", exception);
        }
    }
}
