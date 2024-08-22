package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoResponse.UserResponseDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.UserAlreadyExistsException;
import com.gusdev.library_app.exceptions.UserCantBeDeletedHasLoanException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.repositories.LoanRepository;
import com.gusdev.library_app.repositories.UserRepository;
import com.gusdev.library_app.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    @Autowired
    public UserService(UserRepository userRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    public User create(User user) {
        boolean existUser = userRepository.existsByEmail(user.getEmail());
        if (existUser) {
            throw new UserAlreadyExistsException("User already exists.");
        }
        return userRepository.save(user);
    }

    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.toDTOList(users);
    }

    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.toDTO(user);
    }

    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        } else {
            return UserMapper.toDTO(user);
        }
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
        boolean userHasLoans = loanRepository.existsByUserId(id);
        if(userHasLoans) {
            throw new UserCantBeDeletedHasLoanException("User cannot be deleted because they have active loans.");
        }
        userRepository.delete(user);
    }


}
