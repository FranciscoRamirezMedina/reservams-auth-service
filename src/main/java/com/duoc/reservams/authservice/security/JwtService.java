package com.duoc.reservams.authservice.security;

import com.duoc.reservams.authservice.model.UserAuth;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

// servicio encargado de generar tokens JWT
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(UserAuth userAuth) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        // convierte el texto secreto en una llave valida para firmar el JWT
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(userAuth.getEmail())
                .claim("userId", userAuth.getId())
                .claim("role", userAuth.getRole().getName())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}