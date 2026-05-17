package com.duoc.reservams.authservice.repository;

import com.duoc.reservams.authservice.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// realizar operaciones crud sobre los usuarios de autenticacion
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    // busca un usuario por email para login
    Optional<UserAuth> findByEmail(String email);

    // verifica si ya existe una cuenta con ese email
    boolean existsByEmail(String email);
}