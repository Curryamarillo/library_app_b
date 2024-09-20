package com.gusdev.library_app.secutity;

import com.gusdev.library_app.config.security.JwtUtils;
import com.gusdev.library_app.config.security.UserDetailServiceImpl;
import com.gusdev.library_app.dtoRequest.UserCreateRequestDTO;
import com.gusdev.library_app.dtoRequest.LoginRequestDTO;
import com.gusdev.library_app.dtoResponse.AuthResponseDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class UserDetailServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;


    private User user;

    private String email;

    private String password;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        password = "password123";

        user = User.builder()
                .id(1L)
                .name("User one")
                .surname("Surname one")
                .email("test@example.com")
                .password(passwordEncoder.encode(password))
                .isAdmin(true)
                .build();
    }

    @Test
    void loadUserByUsername_UserExists_ReturnUserDetails() {
        // given
        given(userRepository.findByEmail(email)).willReturn(user);

        // when
        UserDetails userDetails = userDetailService.loadUserByUsername(email);

        // then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }
    @Test
    void createUser_Success_ReturnAuthResponseDTO() {
        // given
        UserCreateRequestDTO userCreateRequestDTO = new UserCreateRequestDTO("Doe", "John", email, false, password);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(jwtUtils.createAuthToken(any(Authentication.class))).willReturn("dummyToken");

        // when
        AuthResponseDTO responseDTO = userDetailService.createUser(userCreateRequestDTO);

        // then
        assertNotNull(responseDTO);
        assertEquals(email, responseDTO.email());
        assertEquals("User created Succesfully", responseDTO.message());
        assertEquals(false, responseDTO.isAdmin());
    }


    @Test
    void loginUserSuccess_ReturnsAuthResponseDTO() {
        // given
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);
        given(userRepository.findByEmail(email)).willReturn(user);
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);
        given(jwtUtils.createAuthToken(any(Authentication.class))).willReturn("dummyToken");

        // when
        AuthResponseDTO responseDTO = userDetailService.loginUser(loginRequestDTO);

        // then
        assertNotNull(responseDTO);
        assertEquals(email, responseDTO.email());
        assertEquals("User logged succesfully", responseDTO.message());
        assertEquals(true, responseDTO.isAdmin());
    }

    @Test
    void loginUserInvalidUsername_ThrowsBadCredentialsException() {
        // given
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(email, password);
        given(userRepository.findByEmail(email)).willReturn(null);

        // when & then
        assertThrows(BadCredentialsException.class, () -> userDetailService.loginUser(loginRequestDTO));
    }


}
