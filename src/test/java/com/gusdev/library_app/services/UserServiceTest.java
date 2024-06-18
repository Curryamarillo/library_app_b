package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoResponse.UserDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.UserAlreadyExistsException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;


import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User user1;
    private UserDTO userDTO;
    @BeforeEach
    void setUp() {
       user1 = User.builder()
                .name("Name one")
                .surname("Surname one")
                .email("one@onemail.com")
                .isAdmin(false)
                .password("password123")
                .build();

       userDTO = UserDTO.builder()
                .name("Name one")
                .surname("Surname one")
                .email("one@onemail.com")
                .isAdmin(false)
                .build();
    }
    @Test
    void createUserTest() {
        // given
        given(userRepository.existsByEmail(user1.getEmail())).willReturn(false);
        given(userRepository.save(user1)).willReturn(user1);

        // when
        User createdUser = userService.create(user1);

        // then
        assertNotNull(createdUser);
        assertEquals(user1.getEmail(), createdUser.getEmail());
        verify(userRepository).existsByEmail(user1.getEmail());
        verify(userRepository).save(user1);
    }
    @Test
    void createUserAlreadyExistsTest() {
        // given
        given(userRepository.existsByEmail(user1.getEmail())).willReturn(true);

        // when & then
        assertThrows(UserAlreadyExistsException.class, () -> userService.create(user1));
        verify(userRepository).existsByEmail(user1.getEmail());
    }
    @Test
    void findAll() {  // given
        List<User> users = List.of(user1);
        given(userRepository.findAll()).willReturn(users);
        given(modelMapper.map(user1, UserDTO.class)).willReturn(userDTO);

        // when
        List<UserDTO> foundUsers = userService.findAll();

        // then
        assertNotNull(foundUsers);
        assertEquals(1, foundUsers.size());
        assertEquals(userDTO.getEmail(), foundUsers.get(0).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void findById() {

        // given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(modelMapper.map(user1, UserDTO.class)).willReturn(userDTO);

        // when
        UserDTO foundUser = userService.findById(user1.getId());

        // then
        assertNotNull(foundUser);
        assertEquals(user1.getEmail(), foundUser.getEmail());
        verify(userRepository).findById(user1.getId());
    }

    @Test
    void update() {   // given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        // when
        user1.setName("Updated Name");
        userService.update(user1.getId(), user1);

        // then
        verify(userRepository).findById(user1.getId());
        verify(userRepository).save(user1);
    }
    @Test
    void updateUserNotFound() {
        // given
        Long nonExistingUserId = 999L;
        given(userRepository.findById(nonExistingUserId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.update(nonExistingUserId, user1));

        // verify that userRepository.findById() was called with the correct ID
        verify(userRepository).findById(nonExistingUserId);

        // verify that userRepository.save() was not called
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser() {
        // given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        // when
        userService.deleteUser(user1.getId());

        // then
        verify(userRepository).findById(user1.getId());
        verify(userRepository).delete(user1);
    }

    @Test
    void convertToDTO() {

        // given
        given(modelMapper.map(user1, UserDTO.class)).willReturn(userDTO);

        // when
        UserDTO mappedUserDTO = userService.convertToDTO(user1);

        // then
        assertNotNull(mappedUserDTO);
        assertEquals(user1.getEmail(), mappedUserDTO.getEmail());
        verify(modelMapper).map(user1, UserDTO.class);
    }
}