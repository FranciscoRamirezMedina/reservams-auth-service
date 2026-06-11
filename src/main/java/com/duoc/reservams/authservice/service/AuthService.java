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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

// Contiene la lógica de registro y login
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

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
        logger.info("Iniciando registro de usuario con email {}", request.getEmail());

        if (userAuthRepository.existsByEmail(request.getEmail())) {
            logger.warn("No se pudo registrar usuario. El email {} ya se encuentra registrado", request.getEmail());
            throw new RuntimeException("El email ya está registrado");
        }

        String roleName = request.getRoleName();

        // Si no se envía rol, registramos como CLIENTE por defecto
        if (roleName == null || roleName.isBlank()) {
            logger.info("No se recibio rol en el registro. Se asignara rol CLIENTE por defecto al email {}", request.getEmail());
            roleName = "CLIENTE";
        }

        logger.info("Buscando rol {} para el registro del usuario {}", roleName, request.getEmail());

        String finalRoleName = roleName;
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> {
                    logger.warn("No se encontro el rol {} para registrar el usuario {}", finalRoleName, request.getEmail());
                    return new RuntimeException("Rol no encontrado");
                });

        UserAuth userAuth = new UserAuth();
        userAuth.setEmail(request.getEmail());
        userAuth.setPassword(passwordEncoder.encode(request.getPassword()));
        userAuth.setEnabled(true);
        userAuth.setCreatedAt(LocalDateTime.now());
        userAuth.setRole(role);

        logger.info("Guardando usuario con email {} y rol {}", userAuth.getEmail(), role.getName());

        UserAuth savedUser = userAuthRepository.save(userAuth);

        logger.info("Usuario registrado correctamente con ID {} y email {}", savedUser.getId(), savedUser.getEmail());

        String token = jwtService.generateToken(savedUser);

        logger.info("Token JWT generado correctamente para usuario ID {}", savedUser.getId());

        return new AuthResponseDTO(
                "Usuario registrado correctamente",
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().getName()
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        logger.info("Iniciando login para email {}", request.getEmail());

        UserAuth userAuth = userAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login fallido. No existe usuario con email {}", request.getEmail());
                    return new RuntimeException("Credenciales inválidas");
                });

        if (!userAuth.getEnabled()) {
            logger.warn("Login rechazado. La cuenta del usuario ID {} se encuentra deshabilitada", userAuth.getId());
            throw new RuntimeException("La cuenta se encuentra deshabilitada");
        }

        logger.info("Validando contraseña para usuario ID {}", userAuth.getId());

        boolean passwordValid = passwordEncoder.matches(
                request.getPassword(),
                userAuth.getPassword()
        );

        if (!passwordValid) {
            logger.warn("Login fallido. Contraseña invalida para usuario ID {}", userAuth.getId());
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(userAuth);

        logger.info("Login correcto. Token JWT generado para usuario ID {}", userAuth.getId());

        return new AuthResponseDTO(
                "Login correcto",
                token,
                userAuth.getId(),
                userAuth.getEmail(),
                userAuth.getRole().getName()
        );
    }
}