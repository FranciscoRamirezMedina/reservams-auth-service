package com.duoc.reservams.authservice.service;

import com.duoc.reservams.authservice.dto.AuthResponseDTO;
import com.duoc.reservams.authservice.dto.LoginRequestDTO;
import com.duoc.reservams.authservice.dto.RegisterRequestDTO;
import com.duoc.reservams.authservice.model.Role;
import com.duoc.reservams.authservice.model.UserAuth;
import com.duoc.reservams.authservice.repository.RoleRepository;
import com.duoc.reservams.authservice.repository.UserAuthRepository;
import com.duoc.reservams.authservice.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// pruebas unitarias para AuthService
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateUserWithDefaultClientRole() {
        // Given
        RegisterRequestDTO request = buildRegisterRequest();
        request.setRoleName(null);

        Role clientRole = buildRole(1L, "CLIENTE");

        when(userAuthRepository.existsByEmail("cliente@test.com")).thenReturn(false);
        when(roleRepository.findByName("CLIENTE")).thenReturn(Optional.of(clientRole));
        when(passwordEncoder.encode("123456")).thenReturn("encrypted-password");

        when(userAuthRepository.save(any(UserAuth.class))).thenAnswer(invocation -> {
            UserAuth userAuth = invocation.getArgument(0);
            userAuth.setId(1L);
            return userAuth;
        });

        when(jwtService.generateToken(any(UserAuth.class))).thenReturn("fake-jwt-token");

        // When
        AuthResponseDTO response = authService.register(request);

        // Then
        assertNotNull(response);
        assertEquals("Usuario registrado correctamente", response.getMessage());
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("cliente@test.com", response.getEmail());
        assertEquals("CLIENTE", response.getRole());

        verify(userAuthRepository, times(1)).existsByEmail("cliente@test.com");
        verify(roleRepository, times(1)).findByName("CLIENTE");
        verify(passwordEncoder, times(1)).encode("123456");
        verify(userAuthRepository, times(1)).save(any(UserAuth.class));
        verify(jwtService, times(1)).generateToken(any(UserAuth.class));
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        // Given
        RegisterRequestDTO request = buildRegisterRequest();

        when(userAuthRepository.existsByEmail("cliente@test.com")).thenReturn(true);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.register(request)
        );

        // Then
        assertEquals("El email ya está registrado", exception.getMessage());

        verify(userAuthRepository, times(1)).existsByEmail("cliente@test.com");
        verifyNoInteractions(roleRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
        verify(userAuthRepository, never()).save(any(UserAuth.class));
    }

    @Test
    void register_shouldThrowException_whenRoleDoesNotExist() {
        // Given
        RegisterRequestDTO request = buildRegisterRequest();
        request.setRoleName("ADMIN");

        when(userAuthRepository.existsByEmail("cliente@test.com")).thenReturn(false);
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.register(request)
        );

        // Then
        assertEquals("Rol no encontrado", exception.getMessage());

        verify(userAuthRepository, times(1)).existsByEmail("cliente@test.com");
        verify(roleRepository, times(1)).findByName("ADMIN");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
        verify(userAuthRepository, never()).save(any(UserAuth.class));
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        // Given
        LoginRequestDTO request = buildLoginRequest();

        UserAuth userAuth = buildUserAuth(1L, "cliente@test.com", "encrypted-password", true, "CLIENTE");

        when(userAuthRepository.findByEmail("cliente@test.com")).thenReturn(Optional.of(userAuth));
        when(passwordEncoder.matches("123456", "encrypted-password")).thenReturn(true);
        when(jwtService.generateToken(userAuth)).thenReturn("fake-jwt-token");

        // When
        AuthResponseDTO response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("Login correcto", response.getMessage());
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("cliente@test.com", response.getEmail());
        assertEquals("CLIENTE", response.getRole());

        verify(userAuthRepository, times(1)).findByEmail("cliente@test.com");
        verify(passwordEncoder, times(1)).matches("123456", "encrypted-password");
        verify(jwtService, times(1)).generateToken(userAuth);
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        // Given
        LoginRequestDTO request = buildLoginRequest();

        when(userAuthRepository.findByEmail("cliente@test.com")).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        // Then
        assertEquals("Credenciales inválidas", exception.getMessage());

        verify(userAuthRepository, times(1)).findByEmail("cliente@test.com");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void login_shouldThrowException_whenPasswordIsInvalid() {
        // Given
        LoginRequestDTO request = buildLoginRequest();

        UserAuth userAuth = buildUserAuth(1L, "cliente@test.com", "encrypted-password", true, "CLIENTE");

        when(userAuthRepository.findByEmail("cliente@test.com")).thenReturn(Optional.of(userAuth));
        when(passwordEncoder.matches("123456", "encrypted-password")).thenReturn(false);

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        // Then
        assertEquals("Credenciales inválidas", exception.getMessage());

        verify(userAuthRepository, times(1)).findByEmail("cliente@test.com");
        verify(passwordEncoder, times(1)).matches("123456", "encrypted-password");
        verifyNoInteractions(jwtService);
    }

    @Test
    void login_shouldThrowException_whenUserIsDisabled() {
        // Given
        LoginRequestDTO request = buildLoginRequest();

        UserAuth userAuth = buildUserAuth(1L, "cliente@test.com", "encrypted-password", false, "CLIENTE");

        when(userAuthRepository.findByEmail("cliente@test.com")).thenReturn(Optional.of(userAuth));

        // When
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(request)
        );

        // Then
        assertEquals("La cuenta se encuentra deshabilitada", exception.getMessage());

        verify(userAuthRepository, times(1)).findByEmail("cliente@test.com");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    private RegisterRequestDTO buildRegisterRequest() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("cliente@test.com");
        request.setPassword("123456");
        request.setRoleName("CLIENTE");
        return request;
    }

    private LoginRequestDTO buildLoginRequest() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("cliente@test.com");
        request.setPassword("123456");
        return request;
    }

    private Role buildRole(Long id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        return role;
    }

    private UserAuth buildUserAuth(Long id, String email, String password, Boolean enabled, String roleName) {
        Role role = buildRole(1L, roleName);

        UserAuth userAuth = new UserAuth();
        userAuth.setId(id);
        userAuth.setEmail(email);
        userAuth.setPassword(password);
        userAuth.setEnabled(enabled);
        userAuth.setCreatedAt(LocalDateTime.now());
        userAuth.setRole(role);

        return userAuth;
    }
}