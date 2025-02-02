package com.dif76oq.AuthMicroservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dif76oq.AuthMicroservice.dto.JwtDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateToken(JwtDTO jwtDTO) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(90).toInstant());

        return JWT.create()
                .withSubject("User info")
                .withClaim("id", jwtDTO.getId())
                .withClaim("username", jwtDTO.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public JwtDTO validateToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey))
                .withSubject("User info").build();

        DecodedJWT jwt = verifier.verify(token);
        JwtDTO jwtDTO = new JwtDTO();
        jwtDTO.setId(jwt.getClaim("id").asInt());
        jwtDTO.setUsername(jwt.getClaim("username").asString());
        return jwtDTO;
    }
}
