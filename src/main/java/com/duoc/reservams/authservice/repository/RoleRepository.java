package com.duoc.reservams.authservice.repository;

import com.duoc.reservams.authservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// realizar operaciones crub sobre la tabla roles
public interface RoleRepository extends JpaRepository<Role, Long> {

    // busca un rol por su nombre, ej cliente
    Optional<Role> findByName(String name);
}