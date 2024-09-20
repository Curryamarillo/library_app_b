package com.gusdev.library_app.dtoResponse;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"email", "message", "isAdmin", "isAuthenticated", "status", "jwt", "refreshJwt"})
public record AuthResponseDTO (
        String email,
        String message,
        Boolean isAuthenticated,
        Boolean isAdmin,
        String authJwt,
        String refreshJwt,
        Boolean status) {}





