package com.gusdev.library_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gusdev.library_app.controller.UserController;
import com.gusdev.library_app.dtoResponse.UserDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.UserCantBeDeletedHasLoanException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.repositories.UserRepository;
import com.gusdev.library_app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc

@Import({UserController.class, UserService.class})
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private UserDTO userDTO1;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setSurname("Doe");
        user1.setEmail("john.doe@example.com");

        userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setName("John");
        userDTO1.setSurname("Doe");
        userDTO1.setEmail("john.doe@example.com");
    }

    @Test
    void createUser_Success() throws Exception {
        given(userService.create(any(User.class))).willReturn(user1);
        given(userService.convertToDTO(user1)).willReturn(userDTO1);

        ResultActions result = mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));

        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDTO1.getId()))
                .andExpect(jsonPath("$.name").value(userDTO1.getName()))
                .andExpect(jsonPath("$.surname").value(userDTO1.getSurname()))
                .andExpect(jsonPath("$.email").value(userDTO1.getEmail()));
    }

    @Test
    void findAllUsers_Success() throws Exception {
        List<UserDTO> userList = new ArrayList<>();
        userList.add(userDTO1);

        given(userService.findAll()).willReturn(userList);

        ResultActions result = mockMvc.perform(get("/users"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(userDTO1.getId()))
                .andExpect(jsonPath("$[0].name").value(userDTO1.getName()))
                .andExpect(jsonPath("$[0].surname").value(userDTO1.getSurname()))
                .andExpect(jsonPath("$[0].email").value(userDTO1.getEmail()));
    }

    @Test
    void findUserById_Success() throws Exception {
        given(userService.findById(1L)).willReturn(userDTO1);

        ResultActions result = mockMvc.perform(get("/users/{id}", 1L));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDTO1.getId()))
                .andExpect(jsonPath("$.name").value(userDTO1.getName()))
                .andExpect(jsonPath("$.surname").value(userDTO1.getSurname()))
                .andExpect(jsonPath("$.email").value(userDTO1.getEmail()));
    }

    @Test
    void findUserById_UserNotFound() throws Exception {
        given(userService.findById(2L)).willThrow(new UserNotFoundException("User not found"));

        ResultActions result = mockMvc.perform(get("/users/{id}", 2L));

        result.andExpect(status().isNotFound());
    }
    @Test
    void findByEmail() throws Exception {
        String user1Email = user1.getEmail();
        given(userService.findByEmail(user1Email)).willReturn(userDTO1);

        ResultActions result = mockMvc.perform(get("/users/email/{email}", user1Email));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDTO1.getId()))
                .andExpect(jsonPath("$.name").value(userDTO1.getName()))
                .andExpect(jsonPath("$.surname").value(userDTO1.getSurname()))
                .andExpect(jsonPath("$.email").value(userDTO1.getEmail()));

    }

    @Test
    void UserNotFoundByEmail() throws Exception {
        String user1Email = user1.getEmail();
        given(userService.findByEmail(user1Email)).willThrow(new UserNotFoundException("User not found"));

        ResultActions result = mockMvc.perform(get("/users/email/{email}", user1Email));
        result.andExpect(status().isNotFound());
    }

    @Test
    void updateUser_Success() throws Exception {
        doNothing().when(userService).update(1L, user1);
        given(userService.findById(1L)).willReturn(userDTO1);

        ResultActions result = mockMvc.perform(put("/users/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDTO1.getId()))
                .andExpect(jsonPath("$.name").value(userDTO1.getName()))
                .andExpect(jsonPath("$.surname").value(userDTO1.getSurname()))
                .andExpect(jsonPath("$.email").value(userDTO1.getEmail()));
    }

    @Test
    void updateUser_UserNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).update(2L, user1);

        ResultActions result = mockMvc.perform(put("/users/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));

        result.andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_Success() throws Exception {
        Long userId = user1.getId();

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }

    @Test
    void deleteUser_UserCantBeDeletedHasLoanException() throws Exception {
        Long userId = user1.getId();

        doThrow(new UserCantBeDeletedHasLoanException("User cannot be deleted because they have active loans."))
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("User cannot be deleted because they have active loans."));
    }

    @Test
    void deleteUser_UserNotFoundException() throws Exception {
        Long userId = user1.getId();

        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User not found"));
    }
}
