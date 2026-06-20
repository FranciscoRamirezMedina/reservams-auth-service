package com.duoc.reservams.authservice.exception;

import com.duoc.reservams.authservice.dto.ErrorResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// pruebas unitarias para GlobalExceptionHandler
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleRuntimeException_shouldReturnBadRequestWithMessage() {
        // Given
        RuntimeException exception = new RuntimeException("Credenciales inválidas");

        // When
        ResponseEntity<ErrorResponseDTO> response =
                globalExceptionHandler.handleRuntimeException(exception);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Credenciales inválidas", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleValidationException_shouldReturnFirstValidationMessage() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError(
                "loginRequestDTO",
                "email",
                "El email es obligatorio"
        );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // When
        ResponseEntity<ErrorResponseDTO> response =
                globalExceptionHandler.handleValidationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("El email es obligatorio", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleValidationException_shouldReturnDefaultMessage_whenNoFieldErrors() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // When
        ResponseEntity<ErrorResponseDTO> response =
                globalExceptionHandler.handleValidationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Error de validacion", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }
}