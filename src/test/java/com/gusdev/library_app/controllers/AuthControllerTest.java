package com.gusdev.library_app.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private AuthService authService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
////    private LoginRequestDTO loginRequestDTO;
////    private LoginResponseDTO loginResponseDTO;
//
//    @BeforeEach
//    void setUp() {
//        loginRequestDTO = new LoginRequestDTO("test@example.com", "password123");
//        loginResponseDTO = new LoginResponseDTO("test@example.com", true);
    }

//    @Test
//    void authenticateUserWhenCredentialsAreCorrectShouldReturnOk() throws Exception {
//        // given
//        given(authService.authenticate(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())).willReturn(loginResponseDTO);
//
//        // when & then
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(loginResponseDTO.getEmail()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$.authenticated").value(loginResponseDTO.isAuthenticated()));
//    }
//    @Test
//    void authenticateUserWhenCredentialsAreIncorrectShouldReturnUnauthorized() throws Exception {
//        // given
//        given(authService.authenticate(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())).willReturn(null);
//
//        // when & then
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(MockMvcResultMatchers.content().string("Email or password incorrect"));
//    }
// }
