package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoRequest.UserCreateRequestDTO;
import com.gusdev.library_app.dtoRequest.UserUpdatePasswordDTO;
import com.gusdev.library_app.dtoRequest.UserUpdateRequestDTO;
import com.gusdev.library_app.dtoResponse.UserResponseDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.InvalidPasswordException;
import com.gusdev.library_app.exceptions.UserCantBeDeletedHasLoanException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        UserResponseDTO createdUser = userService.create(userCreateRequestDTO);
        UserResponseDTO createdUserResponseDTO = new UserResponseDTO(
                createdUser.id(),
                createdUser.name(),
                createdUser.surname(),
                createdUser.email(),
                createdUser.isAdmin()
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

    @Deprecated
    @PutMapping("/v1/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDTO user) {
        try {
            userService.update(id, user);
            UserResponseDTO updatedUser = userService.findById(id);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/v2/update/{id}")
    public ResponseEntity<UserResponseDTO> updateUserV2(@PathVariable Long id, @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        try {
            UserResponseDTO userToUpdate = userService.findById(id);
            userService.update(id, userUpdateRequestDTO);
            UserResponseDTO updatedUser = userService.findById(id);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/update-password/{id}")
    public ResponseEntity<Map<String, String>> updateUserPassword(
            @PathVariable Long id,
            @RequestBody UserUpdatePasswordDTO updatePasswordDTO) {

        Map<String, String> response = new HashMap<>();

        try {
            String message = userService.updatePassword(id, updatePasswordDTO.oldPassword(), updatePasswordDTO.password());
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (InvalidPasswordException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (UserNotFoundException e) {
            response.put("error", "User with ID: " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserCantBeDeletedHasLoanException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
