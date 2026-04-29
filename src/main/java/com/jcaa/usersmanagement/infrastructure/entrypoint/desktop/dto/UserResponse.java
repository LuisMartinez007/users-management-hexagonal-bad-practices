package com.jcaa.usersmanagement.infrastructure.entrypoint.desktop.dto;

// Regla 2 - CORREGIDO: Se usa record en lugar de clase mutable con @Data.
// Regla 15 - CORREGIDO: DTO de salida ahora es inmutable.
public record UserResponse(
        String id,
        String name,
        String email,
        String role,
        String status) {
}