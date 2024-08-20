package com.gusdev.library_app.controller;

import com.gusdev.library_app.config.security.UserDetailServiceImpl;
import com.gusdev.library_app.dtoRequest.CreateUserRequestDTO;
import com.gusdev.library_app.dtoRequest.LoginRequestDTO;
import com.gusdev.library_app.dtoResponse.AuthResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid CreateUserRequestDTO userRequestDTO) {

        return new ResponseEntity<>(this.userDetailService.createUser(userRequestDTO), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        return new ResponseEntity<>(this.userDetailService.loginUser(loginRequestDTO), HttpStatus.OK);
    }

//    @Autowired
//    private final AuthService authService;
//
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @CrossOrigin
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest){
//        LoginResponseDTO userResponseDTO = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
//
//        if (userResponseDTO != null) {
//            return ResponseEntity.ok(userResponseDTO);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password incorrect");
//        }
//    }
}
