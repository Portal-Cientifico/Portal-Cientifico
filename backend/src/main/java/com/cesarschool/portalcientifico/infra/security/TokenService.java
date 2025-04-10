package com.cesarschool.portalcientifico.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cesarschool.portalcientifico.domain.user.User;
import com.cesarschool.portalcientifico.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private final UserRepository userRepository;

    public TokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, LocalDateTime.now().plusMinutes(15)); // Token curto (15 min)
    }

    public String generateRefreshToken(User user) {
        String refreshToken = generateToken(user, LocalDateTime.now().plusDays(7)); // Token longo (7 dias)
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return refreshToken;
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public Optional<User> validateRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    private String generateToken(User user, LocalDateTime expiration) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("login-auth-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(expiration.toInstant(ZoneOffset.of("-03:00")))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token");
        }
    }

    public Optional<User> validateAccessToken(String token) {
        String email = validateToken(token);
        if (email == null) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }

}
