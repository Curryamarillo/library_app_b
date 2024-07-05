package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoResponse.UserDTO;
import com.gusdev.library_app.entities.User;
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
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        User createdUser = userService.create(user);
        UserDTO createdUserDTO = userService.convertToDTO(createdUser);
        return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> usersList = userService.findAll();
        return ResponseEntity.ok(usersList);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        try {
            UserDTO userDTO = userService.findById(id);
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> findByEmail(@PathVariable String email) {
        try {
            UserDTO userDTO = userService.findByEmail(email);
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            return  ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            userService.update(id, user);
            UserDTO updatedUser = userService.findById(id);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
