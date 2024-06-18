package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoResponse.UserDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.UserAlreadyExistsException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public User create(User user) {
        boolean existUser = userRepository.existsByEmail(user.getEmail());
        if (existUser) {
            throw new UserAlreadyExistsException("User already exists.");
        }
        return userRepository.save(user);
    }

    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }

    public void update(Long id, User user) {
        User existsUser = userRepository.findById(id).orElse(null);
        if (existsUser != null) {
            existsUser.setName(user.getName());
            existsUser.setSurname(user.getSurname());
            existsUser.setEmail(user.getEmail());
            existsUser.setIsAdmin(user.getIsAdmin());
            existsUser.setPassword(user.getPassword());
            userRepository.save(existsUser);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        // TODO when Loans services is ready, we have to check if we can delete loans and users
        userRepository.delete(user);
    }

    public UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
