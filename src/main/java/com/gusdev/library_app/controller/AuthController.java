package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoRequest.LoginRequestDTO;
import com.gusdev.library_app.dtoResponse.LoginResponseDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.services.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest){
        LoginResponseDTO userResponseDTO = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

        if (userResponseDTO != null) {
            return ResponseEntity.ok(userResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password incorrect");
        }
    }
}
