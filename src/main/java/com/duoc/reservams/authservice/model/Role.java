package com.duoc.reservams.authservice.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    //identificador unico de rol
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //nombre rol
    @Column(nullable = false, unique = true)
    private String name;
}
