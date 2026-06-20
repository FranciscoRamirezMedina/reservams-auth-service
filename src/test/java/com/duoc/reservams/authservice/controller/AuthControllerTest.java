package com.duoc.reservams.authservice.controller;

import com.duoc.reservams.authservice.dto.AuthResponseDTO;
import com.duoc.reservams.authservice.dto.LoginRequestDTO;
import com.duoc.reservams.authservice.dto.RegisterRequestDTO;
import com.duoc.reservams.authservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// pruebas unitarias para AuthController
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_shouldReturnAuthResponse() {
        // Given
        RegisterRequestDTO request = new RegisterRequestDTO();
        AuthResponseDTO authResponse = mock(AuthResponseDTO.class);

        when(authService.register(request)).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.register(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(authResponse, response.getBody());

        verify(authService, times(1)).register(request);
    }

    @Test
    void login_shouldReturnAuthResponse() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO();
        AuthResponseDTO authResponse = mock(AuthResponseDTO.class);

        when(authService.login(request)).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponseDTO> response = authController.login(request);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(authResponse, response.getBody());

        verify(authService, times(1)).login(request);
    }
}