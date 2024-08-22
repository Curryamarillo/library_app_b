package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoResponse.UserResponseDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.UserCantBeDeletedHasLoanException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User user) {
        User createdUser = userService.create(user);
        UserResponseDTO createdUserResponseDTO = new UserResponseDTO(
                createdUser.getId(),
                createdUser.getName(),
                createdUser.getSurname(),
                createdUser.getEmail(),
                createdUser.getIsAdmin()
        );
        return new ResponseEntity<>(createdUserResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<UserResponseDTO> usersList = userService.findAll();
        return ResponseEntity.ok(usersList);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        try {
            UserResponseDTO userResponseDTO = userService.findById(id);
            return ResponseEntity.ok(userResponseDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> findByEmail(@PathVariable String email) {
        try {
            UserResponseDTO userResponseDTO = userService.findByEmail(email);
            return ResponseEntity.ok(userResponseDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            userService.update(id, user);
            UserResponseDTO updatedUser = userService.findById(id);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserCantBeDeletedHasLoanException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User cannot be deleted because they have active loans.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
