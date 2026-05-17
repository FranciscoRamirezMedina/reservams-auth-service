package com.duoc.reservams.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

// respuesta simple para errores de la API
@Data
@AllArgsConstructor
public class ErrorResponseDTO {

    private String message;
    private int status;
    private LocalDateTime timestamp;
}