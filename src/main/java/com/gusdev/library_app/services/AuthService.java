package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoResponse.LoginResponseDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    public LoginResponseDTO authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) { return null; };
        if(Objects.equals(user.getEmail(), email) && Objects.equals(user.getPassword(), password)) {
            return new LoginResponseDTO(user.getEmail(), true);
        }
        return null;
    }
}
