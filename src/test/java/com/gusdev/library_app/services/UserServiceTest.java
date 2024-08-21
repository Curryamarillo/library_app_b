package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoResponse.UserDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.UserAlreadyExistsException;
import com.gusdev.library_app.exceptions.UserCantBeDeletedHasLoanException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.repositories.LoanRepository;
import com.gusdev.library_app.repositories.UserRepository;
import com.gusdev.library_app.utils.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private LoanRepository loanRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private UserDTO userDTO1;
    private UserDTO userDTO2;



    @BeforeEach
    void setUp() {
       user1 = User.builder()
                .name("Name one")
                .surname("Surname one")
                .email("one@onemail.com")
                .isAdmin(false)
                .password("password123")
                .build();
        user2 = User.builder()
                .name("Name two")
                .surname("Surname two")
                .email("two@onemail.com")
                .isAdmin(true)
                .password("password234")
                .build();

       userDTO1 = new UserDTO(1L,"Name one", "Surname one","one@onamail.com", false);
       userDTO2 = new UserDTO(2L, "Name two", "Surname two", "two@onemail.com", true);
//
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
        List<User> users = List.of(user1, user2);
        List<UserDTO> userDTOList = List.of(userDTO1, userDTO2);
        given(userRepository.findAll()).willReturn(users);
        given(UserMapper.toDTOList(users)).willReturn(userDTOList);

        // when
        List<UserDTO> foundUsers = userService.findAll();

        // then
        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        assertEquals(userDTOList, foundUsers);
        verify(userRepository).findAll();
    }

    @Test
    void findByEmailTest() {
        // given
        given(userRepository.findByEmail(user1.getEmail())).willReturn(user1);
        given(UserMapper.toDTO(user1)).willReturn(userDTO1);

        // when
        UserDTO foundUser = userService.findByEmail(user1.getEmail());

        // then
        assertNotNull(foundUser);
        assertEquals(userDTO1.email(), foundUser.email());
        verify(userRepository).findByEmail(user1.getEmail());
    }
    @Test
    void findByEmailWhenUserNotFoundShouldReturnNull() {
        // given
        String email = "nonexistent@example.com";
        given(userRepository.findByEmail(email)).willReturn(null);

        // when
        UserDTO foundUser = userService.findByEmail(email);

        // then
        assertNull(foundUser);
        verify(userRepository).findByEmail(email);
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
        assertEquals(user1.getEmail(), foundUser.email());
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
        given(loanRepository.existsByUserId(user1.getId())).willReturn(false);
        // when
        userService.deleteUser(user1.getId());

        // then
        verify(userRepository).findById(user1.getId());
        verify(userRepository).delete(user1);
    }

    @Test
    void deleteUserWithLoan() {
        //given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given((loanRepository.existsByUserId(user1.getId()))).willReturn(true);

        //when & then
        assertThrows(UserCantBeDeletedHasLoanException.class, () ->{userService.deleteUser(user1.getId());});
        verify(userRepository, never()).delete(user1);


    }

    @Test
    void convertToDTO() {

        // given
        given(modelMapper.map(user1, UserDTO.class)).willReturn(userDTO);

        // when
        UserDTO mappedUserDTO = userService.convertToDTO(user1);

        // then
        assertNotNull(mappedUserDTO);
        assertEquals(user1.getEmail(), mappedUserDTO.email());
        verify(modelMapper).map(user1, UserDTO.class);
    }
}