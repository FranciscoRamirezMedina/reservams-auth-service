package com.duoc.reservams.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// respuesta que se devuelve al registrar o iniciar sesion
@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String message;
    private String token;
    private Long userId;
    private String email;
    private String role;
}