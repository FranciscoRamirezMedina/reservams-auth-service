package com.duoc.reservams.authservice.controller;

import com.duoc.reservams.authservice.dto.AuthResponseDTO;
import com.duoc.reservams.authservice.dto.LoginRequestDTO;
import com.duoc.reservams.authservice.dto.RegisterRequestDTO;
import com.duoc.reservams.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controlador REST para autenticación
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Registra un usuario nuevo
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // Inicia sesión y devuelve un token JWT
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}