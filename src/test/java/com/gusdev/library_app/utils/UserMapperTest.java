package com.gusdev.library_app.utils;

import com.gusdev.library_app.dtoResponse.UserResponseDTO;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    User user1 = new User();
    User user2 = new User();


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
        user2.setId(1L);
        user2.setName("User Two");
        user2.setSurname("Surname Two");
        user2.setEmail("emailTwo@test.com");
        user2.setIsAdmin(false);
        user2.setLoans(Set.of(new Loan()));
        user2.setPassword("password456");
    }

    @Test
    void toDTO() {
        // when
        UserResponseDTO userResponseDTO = UserMapper.toDTO(user1);
        // then
        assertNotNull(userResponseDTO);
        assertEquals(user1.getId(), userResponseDTO.id());
        assertEquals(user1.getName(), userResponseDTO.name());
        assertEquals(user1.getSurname(), userResponseDTO.surname());
        assertEquals(user1.getIsAdmin(), userResponseDTO.isAdmin());

    }

    @Test
    void toDTOList() {
        // given
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        List<UserResponseDTO> userResponseDTOList = UserMapper.toDTOList(userList);

        assertNotNull(userResponseDTOList);
        assertEquals(2, userList.size());

        UserResponseDTO dto1 = userResponseDTOList.getFirst();
        assertEquals(user1.getId(), dto1.id());
        assertEquals(user1.getName(), dto1.name());
        assertEquals(user1.getSurname(), dto1.surname());
        assertEquals(user1.getEmail(), dto1.email());
        assertEquals(user1.getIsAdmin(), dto1.isAdmin());

        UserResponseDTO dto2 = userResponseDTOList.get(1);
        assertEquals(user2.getId(), dto2.id());
        assertEquals(user2.getName(), dto2.name());
        assertEquals(user2.getSurname(), dto2.surname());
        assertEquals(user2.getEmail(), dto2.email());
        assertEquals(user2.getIsAdmin(), dto2.isAdmin());
    }
}