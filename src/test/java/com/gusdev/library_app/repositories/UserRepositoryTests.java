package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    public void setUpTest() {
        user1 = User.builder()
                .name("Name one")
                .surname("Surname one")
                .email("one@onemail.com")
                .isAdmin(false)
                .password("password123")
                .build();

        userRepository.save(user1);
    }

    @Test
    public void saveUserTest() {
        // given
        User user2 = User.builder()
                .name("Jane")
                .surname("Doe")
                .email("jane.doe@example.com")
                .isAdmin(true)
                .password("securepassword")
                .build();

        // when
        User savedUser = userRepository.save(user2);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void existsByEmailTest() {
        // when
        boolean exists = userRepository.existsByEmail("john.doe@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    public void testCreateReadUpdateDelete() {
        // Create
        User newUser = User.builder()
                .name("Alice")
                .surname("Smith")
                .email("alice.smith@example.com")
                .isAdmin(false)
                .password("alicepassword")
                .build();
        userRepository.save(newUser);
        assertThat(userRepository.existsByEmail("alice.smith@example.com")).isTrue();

        // Read
        User foundUser = userRepository.findById(newUser.getId()).orElse(null);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("alice.smith@example.com");

        // Update
        foundUser.setName("Alicia");
        userRepository.save(foundUser);
        User updatedUser = userRepository.findById(foundUser.getId()).orElse(null);
        assertThat(updatedUser.getName()).isEqualTo("Alicia");

        // Delete
        userRepository.delete(updatedUser);
        assertThat(userRepository.existsByEmail("alice.smith@example.com")).isFalse();
    }
}
