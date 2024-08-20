package com.gusdev.library_app.services;

// import com.gusdev.library_app.dtoResponse.LoginResponseDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private String email;
    private String password;
    private String incorrectPassword;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        email = "test@example.com";
        password = "password123";
        incorrectPassword = "wrongpassword";

        // Instancia el objeto User aquí
        user = new User();
        user.setEmail(email);
        user.setPassword(password);
    }

    @Test
    void authenticateWhenCredentialsAreCorrectShouldReturnLoginResponse() {
//        // given
//        given(userRepository.findByEmail(email)).willReturn(user);
//
//        // when
//        LoginResponseDTO response = authService.authenticate(email, password);
//
//        // then
//        assertNotNull(response);
//        assertEquals(email, response.getEmail());
//        assertTrue(response.isAuthenticated());
    }

    @Test
    void authenticateWhenUserDoesNotExistShouldReturnNull() {
//        // given
//        given(userRepository.findByEmail(email)).willReturn(null);
//
//        // when
//        LoginResponseDTO responseDTO = authService.authenticate(email, password);
//
//        // then
//        assertNull(responseDTO);
    }

    @Test
    void authenticateWhenPasswordIsIncorrectShouldReturnNull() {
//        // given
//        given(userRepository.findByEmail(email)).willReturn(user);
//
//        // when
//        LoginResponseDTO responseDTO = authService.authenticate(email, incorrectPassword);
//
//        // then
//        assertNull(responseDTO);
    }
}
