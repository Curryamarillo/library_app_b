package com.gusdev.library_app.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gusdev.library_app.config.security.JwtUtils;
import com.gusdev.library_app.config.security.UserDetailServiceImpl;
import com.gusdev.library_app.controller.AuthController;
import com.gusdev.library_app.dtoRequest.UserCreateRequestDTO;
import com.gusdev.library_app.dtoRequest.LoginRequestDTO;
import com.gusdev.library_app.dtoResponse.AuthResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {


    @InjectMocks
    private AuthController authController;

   @Autowired
   private MockMvc mockMvc;

   @MockBean
    private UserDetailServiceImpl userDetailService;

   @Autowired
   private ObjectMapper objectMapper;

   @Autowired
   private JwtUtils jwtUtils;

  private LoginRequestDTO loginRequestDTO;
  private AuthResponseDTO authResponseDTO;
  private UserCreateRequestDTO userCreateRequestDTO;

   @BeforeEach
   void setUp() {
       userCreateRequestDTO = new UserCreateRequestDTO(
               "UserOne",
               "UserSurnameOne",
               "email@email.com",
               true,
               "password123");
       loginRequestDTO = new LoginRequestDTO(
               "test@example.com",
               "password123");
       authResponseDTO = new AuthResponseDTO("test@example.com",
               "User created Successfully",
               true,
               true,
               "mocked-jwt-token",
               "mocked-refresh-jwt",
               true);
    }

    private String generateJwtToken() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("emailOne@test.com", null, List.of(new
                SimpleGrantedAuthority("ROLE_ADMIN")));
        return jwtUtils.createAuthToken(auth);
    }

    @Test
    void UserRegisterSuccessful() throws Exception {
       String token = generateJwtToken(); when(userDetailService.createUser(any(UserCreateRequestDTO.class))).thenReturn(authResponseDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateRequestDTO)))
                .andExpect(status().isCreated());
    }
    @Test
    void login_Success() throws Exception {
        // Simular la respuesta del servicio
        when(userDetailService.loginUser(any(LoginRequestDTO.class))).thenReturn(authResponseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk());
    }
    @Test
    void login_Failure_InvalidCredentials() throws Exception {
        when(userDetailService.loginUser(any(LoginRequestDTO.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    assertEquals("Invalid credentials", Objects.requireNonNull(result.getResolvedException()).getMessage());
                });
    }
 }
