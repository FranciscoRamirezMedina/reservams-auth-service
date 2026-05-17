package com.duoc.reservams.authservice.service;

import com.duoc.reservams.authservice.dto.AuthResponseDTO;
import com.duoc.reservams.authservice.dto.LoginRequestDTO;
import com.duoc.reservams.authservice.dto.RegisterRequestDTO;
import com.duoc.reservams.authservice.model.Role;
import com.duoc.reservams.authservice.model.UserAuth;
import com.duoc.reservams.authservice.repository.RoleRepository;
import com.duoc.reservams.authservice.repository.UserAuthRepository;
import com.duoc.reservams.authservice.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Contiene la lógica de registro y login
@Service
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserAuthRepository userAuthRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userAuthRepository = userAuthRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userAuthRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        String roleName = request.getRoleName();

        // Si no se envía rol, registramos como CLIENTE por defecto
        if (roleName == null || roleName.isBlank()) {
            roleName = "CLIENTE";
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        UserAuth userAuth = new UserAuth();
        userAuth.setEmail(request.getEmail());
        userAuth.setPassword(passwordEncoder.encode(request.getPassword()));
        userAuth.setEnabled(true);
        userAuth.setCreatedAt(LocalDateTime.now());
        userAuth.setRole(role);

        UserAuth savedUser = userAuthRepository.save(userAuth);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponseDTO(
                "Usuario registrado correctamente",
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().getName()
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        UserAuth userAuth = userAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!userAuth.getEnabled()) {
            throw new RuntimeException("La cuenta se encuentra deshabilitada");
        }

        boolean passwordValid = passwordEncoder.matches(
                request.getPassword(),
                userAuth.getPassword()
        );

        if (!passwordValid) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(userAuth);

        return new AuthResponseDTO(
                "Login correcto",
                token,
                userAuth.getId(),
                userAuth.getEmail(),
                userAuth.getRole().getName()
        );
    }
}