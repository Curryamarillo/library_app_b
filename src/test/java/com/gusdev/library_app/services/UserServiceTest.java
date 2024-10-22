package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoRequest.UserCreateRequestDTO;
import com.gusdev.library_app.dtoRequest.UserUpdateRequestDTO;
import com.gusdev.library_app.dtoResponse.UserResponseDTO;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.InvalidPasswordException;
import com.gusdev.library_app.exceptions.UserAlreadyExistsException;
import com.gusdev.library_app.exceptions.UserCantBeDeletedHasLoanException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.repositories.BookRepository;
import com.gusdev.library_app.repositories.LoanRepository;
import com.gusdev.library_app.repositories.UserRepository;
import com.gusdev.library_app.utils.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private UserService userService;


    private User user1;
    private User user2;
    private UserCreateRequestDTO userCreateRequestDTO1;
    private UserUpdateRequestDTO userUpdateRequestDTO;
    private UserResponseDTO userResponseDTO1;
    private UserResponseDTO userResponseDTO2;
    private List<String> bookList;


    @BeforeEach
    void setUp() {
        // Usuario 1 - Admin
        user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setSurname("Doe");
        user1.setEmail("john.doe@test.com");
        user1.setIsAdmin(true);
        user1.setLoans(Set.of(new Loan()));
        user1.setPassword("passwordJohn");

        // Usuario 2 - No Admin
        user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setSurname("Doe");
        user2.setEmail("jane.doe@test.com");
        user2.setIsAdmin(false);
        user2.setLoans(Set.of(new Loan()));
        user2.setPassword("passwordJane");

        // Usuario 3 - Admin
        User user3 = new User();
        user3.setId(3L);
        user3.setName("Alex");
        user3.setSurname("Smith");
        user3.setEmail("alex.smith@test.com");
        user3.setIsAdmin(true);
        user3.setLoans(Set.of());
        user3.setPassword("passwordAlex");

        // DTOs para creación de usuarios
        userCreateRequestDTO1 = new UserCreateRequestDTO(
                "John",
                "Doe",
                "john.doe@test.com",
                true,   // Es Admin
                "passwordJohn"
        );

        // No es Admin
        UserCreateRequestDTO userCreateRequestDTO2 = new UserCreateRequestDTO(
                "Jane",
                "Doe",
                "jane.doe@test.com",
                false,  // No es Admin
                "passwordJane"
        );

        // Es Admin
        UserCreateRequestDTO userCreateRequestDTO3 = new UserCreateRequestDTO(
                "Alex",
                "Smith",
                "alex.smith@test.com",
                true,   // Es Admin
                "passwordAlex"
        );
        userUpdateRequestDTO = new UserUpdateRequestDTO(
                "Peter",
                "Garrison",
                "peter@email.com",
                false
        );

        // Respuestas para los usuarios
        userResponseDTO1 = new UserResponseDTO(
                1L,
                "John",
                "Doe",
                "john.doe@test.com",
                true   // Admin
        );

        userResponseDTO2 = new UserResponseDTO(
                2L,
                "Jane",
                "Doe",
                "jane.doe@test.com",
                false  // No Admin
        );

        // Admin
        UserResponseDTO userResponseDTO3 = new UserResponseDTO(
                3L,
                "Alex",
                "Smith",
                "alex.smith@test.com",
                true   // Admin
        );

        bookList = List.of("Book one", "Book two", "Book three");

    }
    @Test
    void createUserTest() {
        // given
        given(userRepository.existsByEmail(userCreateRequestDTO1.email())).willReturn(false);

        User newUser = new User();
        newUser.setName(userCreateRequestDTO1.name());
        newUser.setSurname(userCreateRequestDTO1.surname());
        newUser.setEmail(userCreateRequestDTO1.email());
        newUser.setIsAdmin(userCreateRequestDTO1.isAdmin());
        newUser.setPassword(passwordEncoder.encode(userCreateRequestDTO1.password()));
        newUser.setLoans(Set.of());

        given(userRepository.save(any(User.class))).willReturn(newUser);
        given(userRepository.findByEmail(newUser.getEmail())).willReturn(newUser);

        // when
        UserResponseDTO userResponseDTO = userService.create(userCreateRequestDTO1);

        // then
        assertNotNull(userResponseDTO);
        assertEquals(newUser.getEmail(), userResponseDTO.email());
        verify(userRepository).existsByEmail(userCreateRequestDTO1.email());
        verify(userRepository).save(any(User.class));
        verify(userRepository).findByEmail(newUser.getEmail());
    }



    @Test
    void createUserAlreadyExistsTest() {
        // given
        given(userRepository.existsByEmail(user1.getEmail())).willReturn(true);

        // when & then
        assertThrows(UserAlreadyExistsException.class, () -> userService.create(userCreateRequestDTO1));
        verify(userRepository).existsByEmail(user1.getEmail());
    }
    @Test
    void findAll() {
        try (MockedStatic<UserMapper> mockedMapper = Mockito.mockStatic(UserMapper.class)) {
            // given
            List<User> users = List.of(user1, user2);
            List<UserResponseDTO> userResponseDTOList = List.of(userResponseDTO1, userResponseDTO2);
            given(userRepository.findAll()).willReturn(users);
            mockedMapper.when(() -> UserMapper.toDTOList(users)).thenReturn(userResponseDTOList);

            // when
            List<UserResponseDTO> foundUsers = userService.findAll();

            // then
            assertNotNull(foundUsers);
            assertEquals(2, foundUsers.size());
            assertEquals(userResponseDTOList, foundUsers);
            verify(userRepository).findAll();
        }
    }

    @Test
    void findByEmailTest() {
        try (MockedStatic<UserMapper> mockedMapper = Mockito.mockStatic(UserMapper.class)) {
            // given
            given(userRepository.findByEmail(user1.getEmail())).willReturn(user1);
            mockedMapper.when(() -> UserMapper.toDTO(user1)).thenReturn(userResponseDTO1);

            // when
            UserResponseDTO foundUser = userService.findByEmail(userResponseDTO1.email());

            // then
            assertNotNull(foundUser);
            assertEquals(userResponseDTO1.email(), foundUser.email());
            verify(userRepository).findByEmail(user1.getEmail());
        }
    }
    @Test
    void findByEmailWhenUserNotFoundShouldReturnNull() {
        // given
        String email = "nonexistent@example.com";
        given(userRepository.findByEmail(email)).willReturn(null);

        // when
        UserResponseDTO foundUser = userService.findByEmail(email);

        // then
        assertNull(foundUser);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void findById() {

        // given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        // when
        UserResponseDTO foundUser = userService.findById(user1.getId());

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
        userService.update(user1.getId(), userUpdateRequestDTO);

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
        assertThrows(UserNotFoundException.class, () -> userService.update(nonExistingUserId, userUpdateRequestDTO));

        // verify that userRepository.findById() was called with the correct ID
        verify(userRepository).findById(nonExistingUserId);

        // verify that userRepository.save() was not called
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser() {
        // given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(loanRepository.existsByUserIdAndReturnDateIsNull(user1.getId())).willReturn(false);
        given(bookRepository.findBookTitlesByUserId(user1.getId())).willReturn(List.of());

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
        given((loanRepository.existsByUserIdAndReturnDateIsNull(user1.getId()))).willReturn(true);
        given(bookRepository.findBookTitlesByUserId(user1.getId())).willReturn(bookList);

        //when & then
       UserCantBeDeletedHasLoanException exception = assertThrows(UserCantBeDeletedHasLoanException.class, () -> {
           userService.deleteUser(user1.getId());
       });
        verify(userRepository, never()).delete(user1);
        assertTrue(exception.getMessage().contains("Book one"));
        assertTrue(exception.getMessage().contains("Book two"));
    }

    @Test
    void getPasswordShouldReturnPasswordWhenUserExists() {
        // given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        // when
        String password = userService.getPassword(user1.getId());

        // then

        assertEquals("passwordJohn", password);
    }
    @Test
    void getPasswordShouldReturnUserNotFoundException() {
        // given
        given(userRepository.findById((user1.getId()))).willReturn(Optional.empty());

        // then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getPassword(user1.getId());
        });
        // then
        assertTrue(exception.getMessage().contains("User with id: " + user1.getId() + " not found"));
    }

    @Test
    void updatePasswordSuccess() {

       user1.setPassword("oldPassword");
       when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
       when(passwordEncoder.matches(eq("oldPassword"), anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user1);
        doReturn("newEncodedPassword").when(passwordEncoder).encode("newPassword");


        String result = userService.updatePassword(1L,"oldPassword", "newPassword");

       assertEquals("oldPassword", result);
       assertEquals("newEncodedPassword", user1.getPassword());
       verify(userRepository).save(user1);
    }


}