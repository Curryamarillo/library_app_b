package com.gusdev.library_app.dtoResponse;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"email", "message", "isAuthenticated", "status", "jwt"})
public record AuthResponseDTO (
        String email,
        String message,
        Boolean isAuthenticated,
        String jwt,
        Boolean status) {}





