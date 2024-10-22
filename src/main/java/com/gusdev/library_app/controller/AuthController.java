package com.gusdev.library_app.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gusdev.library_app.config.security.JwtUtils;
import com.gusdev.library_app.config.security.UserDetailServiceImpl;
import com.gusdev.library_app.dtoRequest.UserCreateRequestDTO;
import com.gusdev.library_app.dtoRequest.LoginRequestDTO;
import com.gusdev.library_app.dtoResponse.AuthResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid UserCreateRequestDTO userRequestDTO) {

        return new ResponseEntity<>(this.userDetailService.createUser(userRequestDTO), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        return new ResponseEntity<>(this.userDetailService.loginUser(loginRequestDTO), HttpStatus.OK);
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authorization){
        System.out.println("Inside the Post Mapping Method Refresh Token");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Missing or invalid authorization header"));
        }
        String refreshToken = authorization.substring(7);
        try {
            DecodedJWT decodedJWT = jwtUtils.validateToken(refreshToken);
            String username = jwtUtils.extractUsername(decodedJWT);

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, AuthorityUtils.commaSeparatedStringToAuthorityList(decodedJWT.getClaim("authorities").asString()));

            String newAccessToken = jwtUtils.createAuthToken(authentication);

            return  ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Invalid refresh token"));
        }
    }
}
