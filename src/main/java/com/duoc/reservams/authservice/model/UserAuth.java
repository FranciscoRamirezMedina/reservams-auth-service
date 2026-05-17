package com.duoc.reservams.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// representa las credenciales de acceso de un usuario
@Entity
@Table(name = "users_auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // correo usado para iniciar sesion
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // contraseña cifrada con BCrypt
    @Column(nullable = false)
    private String password;

    // permite activar o desactivar una cuenta
    @Column(nullable = false)
    private Boolean enabled = true;

    // fecha de creación del usuario
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // muchos usuarios pueden compartir el mismo rol
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}