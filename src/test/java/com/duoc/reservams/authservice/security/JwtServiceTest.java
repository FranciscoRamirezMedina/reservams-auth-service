package com.duoc.reservams.authservice.security;

import com.duoc.reservams.authservice.model.Role;
import com.duoc.reservams.authservice.model.UserAuth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

// pruebas unitarias para JwtService
class JwtServiceTest {

    @Test
    void generateToken_shouldCreateValidJwtToken() {
        // Given
        JwtService jwtService = new JwtService();

        String secret = "12345678901234567890123456789012";
        Long expiration = 3600000L;

        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expiration", expiration);

        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENTE");

        UserAuth userAuth = new UserAuth();
        userAuth.setId(1L);
        userAuth.setEmail("cliente@test.com");
        userAuth.setRole(role);

        // When
        String token = jwtService.generateToken(userAuth);

        // Then
        assertNotNull(token);
        assertFalse(token.isBlank());

        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("cliente@test.com", claims.getSubject());
        assertEquals(1, claims.get("userId"));
        assertEquals("CLIENTE", claims.get("role"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}