package com.gusdev.library_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gusdev.library_app.config.security.JwtUtils;
import com.gusdev.library_app.controller.UserController;
import com.gusdev.library_app.dtoResponse.UserResponseDTO;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.UserCantBeDeletedHasLoanException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import({UserController.class, UserService.class, JwtUtils.class})
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    private User user1;
    private User user2;
    private UserResponseDTO userResponseDTO1;
    private UserResponseDTO userResponseDTO2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("User One");
        user1.setSurname("Surname One");
        user1.setEmail("emailOne@test.com");
        user1.setIsAdmin(true);
        user1.setLoans(Set.of(new Loan()));
        user1.setPassword("password123");


        user2 = new User();
        user2.setId(2L);
        user2.setName("User Two");
        user2.setSurname("Surname Two");
        user2.setEmail("emailTwo@test.com");
        user2.setIsAdmin(false);
        user2.setLoans(Set.of(new Loan()));
        user2.setPassword("password456");

        userResponseDTO1 = new UserResponseDTO(1L, "User One", "Surname One", "emailOne@test.com", true);
        userResponseDTO2 = new UserResponseDTO(2L, "User Two", "Surname Two", "emailTwo@test.com", false);
    }

    private String generateJwtToken() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("emailOne@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        return "Bearer " + jwtUtils.createAuthToken(auth);
    }

    @Test
    void createUser_Success() throws Exception {
        String token = generateJwtToken();

        given(userService.create(any(User.class))).willReturn(user1);


        ResultActions result = mockMvc.perform(post("/users/create")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResponseDTO1)));

        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponseDTO1.id()))
                .andExpect(jsonPath("$.name").value(userResponseDTO1.name()))
                .andExpect(jsonPath("$.surname").value(userResponseDTO1.surname()))
                .andExpect(jsonPath("$.email").value(userResponseDTO1.email()));
    }

    @Test
    void findAllUsers_Success() throws Exception {
        String token = generateJwtToken();

        List<UserResponseDTO> userList = List.of(userResponseDTO1, userResponseDTO2);

        given(userService.findAll()).willReturn(userList);

        ResultActions result = mockMvc.perform(get("/users")
                .header("Authorization", token));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userResponseDTO1.id()))
                .andExpect(jsonPath("$[0].name").value(userResponseDTO1.name()))
                .andExpect(jsonPath("$[0].surname").value(userResponseDTO1.surname()))
                .andExpect(jsonPath("$[0].email").value(userResponseDTO1.email()))
                .andExpect(jsonPath("$[1].id").value(userResponseDTO2.id()))
                .andExpect(jsonPath("$[1].name").value(userResponseDTO2.name()))
                .andExpect(jsonPath("$[1].surname").value(userResponseDTO2.surname()))
                .andExpect(jsonPath("$[1].email").value(userResponseDTO2.email()));
    }

    @Test
    void findUserById_Success() throws Exception {
        String token = generateJwtToken();

        given(userService.findById(1L)).willReturn(userResponseDTO1);

        ResultActions result = mockMvc.perform(get("/users/{id}", 1L)
                .header("Authorization", token));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponseDTO1.id()))
                .andExpect(jsonPath("$.name").value(userResponseDTO1.name()))
                .andExpect(jsonPath("$.surname").value(userResponseDTO1.surname()))
                .andExpect(jsonPath("$.email").value(userResponseDTO1.email()));
    }

    @Test
    void findUserById_UserNotFound() throws Exception {
        String token = generateJwtToken();

        given(userService.findById(2L)).willThrow(new UserNotFoundException("User not found"));

        ResultActions result = mockMvc.perform(get("/users/{id}", 2L)
                .header("Authorization", token));

        result.andExpect(status().isNotFound());
    }

    @Test
    void findByEmail() throws Exception {
        String token = generateJwtToken();

        String user1Email = user1.getEmail();
        given(userService.findByEmail(user1Email)).willReturn(userResponseDTO1);

        ResultActions result = mockMvc.perform(get("/users/email/{email}", user1Email)
                .header("Authorization", token));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponseDTO1.id()))
                .andExpect(jsonPath("$.name").value(userResponseDTO1.name()))
                .andExpect(jsonPath("$.surname").value(userResponseDTO1.surname()))
                .andExpect(jsonPath("$.email").value(userResponseDTO1.email()));
    }

    @Test
    void UserNotFoundByEmail() throws Exception {
        String token = generateJwtToken();

        String user1Email = user1.getEmail();
        given(userService.findByEmail(user1Email)).willThrow(new UserNotFoundException("User not found"));

        ResultActions result = mockMvc.perform(get("/users/email/{email}", user1Email)
                .header("Authorization", token));

        result.andExpect(status().isNotFound());
    }

    @Test
    void updateUser_Success() throws Exception {
        String token = generateJwtToken();

        doNothing().when(userService).update(1L, user1);
        given(userService.findById(1L)).willReturn(userResponseDTO1);

        ResultActions result = mockMvc.perform(put("/users/{id}", 1L)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResponseDTO1)));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponseDTO1.id()))
                .andExpect(jsonPath("$.name").value(userResponseDTO1.name()))
                .andExpect(jsonPath("$.surname").value(userResponseDTO1.surname()))
                .andExpect(jsonPath("$.email").value(userResponseDTO1.email()));
    }

    @Test
    void updateUser_UserNotFound() throws Exception {
        String token = generateJwtToken();

        doThrow(new UserNotFoundException("User not found")).when(userService).update(2L, user2);

        ResultActions result = mockMvc.perform(put("/users/{id}", 2L)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userResponseDTO2)));

        result.andExpect(status().isOk());
    }

    @Test
    void deleteUser_Success() throws Exception {
        String token = generateJwtToken();

        Long userId = user1.getId();

        mockMvc.perform(delete("/users/{id}", userId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_UserCantBeDeletedHasLoanException() throws Exception {
        String token = generateJwtToken();

        Long userId = user1.getId();

        doThrow(new UserCantBeDeletedHasLoanException("User cannot be deleted because they have active loans."))
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId)
                        .header("Authorization", token))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("User cannot be deleted because they have active loans."));
    }

    @Test
    void deleteUser_UserNotFoundException() throws Exception {
        String token = generateJwtToken();

        Long userId = user1.getId();

        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId)
                        .header("Authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User not found"));
    }
}


