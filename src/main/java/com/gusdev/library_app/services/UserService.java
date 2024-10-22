package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoRequest.UserCreateRequestDTO;
import com.gusdev.library_app.dtoRequest.UserUpdateRequestDTO;
import com.gusdev.library_app.dtoResponse.UserResponseDTO;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.InvalidPasswordException;
import com.gusdev.library_app.exceptions.UserAlreadyExistsException;
import com.gusdev.library_app.exceptions.UserCantBeDeletedHasLoanException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.repositories.BookRepository;
import com.gusdev.library_app.repositories.LoanRepository;
import com.gusdev.library_app.repositories.UserRepository;
import com.gusdev.library_app.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;


    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, LoanRepository loanRepository, BookRepository bookRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO create(UserCreateRequestDTO userCreateRequestDTO) {
        boolean existUser = userRepository.existsByEmail(userCreateRequestDTO.email());
        if (existUser) {
            throw new UserAlreadyExistsException("User already exists.");
        }
        User newUser = new User();
        newUser.setName(userCreateRequestDTO.name());
        newUser.setSurname(userCreateRequestDTO.surname());
        newUser.setEmail(userCreateRequestDTO.email());
        String encodedPassword = passwordEncoder.encode(userCreateRequestDTO.password());
        newUser.setPassword(encodedPassword);
        newUser.setIsAdmin(userCreateRequestDTO.isAdmin());
        userRepository.save(newUser);

        User user = userRepository.findByEmail(newUser.getEmail());
        return new UserResponseDTO(user.getId(), user.getName(),user.getSurname(),user.getEmail(),user.getIsAdmin());
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

    public String getPassword(Long id) {
        return userRepository.findById(id)
                .map(User::getPassword)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }

    public void update(Long id, UserUpdateRequestDTO dto) {
        User existsUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        System.out.println("DTO received: " + dto.getClass().getName());

        existsUser.setName(dto.name());
        existsUser.setSurname(dto.surname());
        existsUser.setEmail(dto.email());
        existsUser.setIsAdmin(dto.isAdmin());
        userRepository.save(existsUser);
    }

    public String updatePassword(Long id, String oldPassword, String newPassword) {
        String oldPasswordInDb = getPassword(id);

        if (passwordEncoder.matches(oldPassword, oldPasswordInDb)) {
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            User existentUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id: " + id + "not found"));
            existentUser.setPassword(encodedNewPassword);
            userRepository.save(existentUser);

        } else {
            throw new InvalidPasswordException("Old password does not match");
        }
        return oldPasswordInDb;
    }



    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        boolean userHasActiveLoans = loanRepository.existsByUserIdAndReturnDateIsNull(id);
        List<String> bookTitles = bookRepository.findBookTitlesByUserId(id);
        loanRepository.deleteByUserId(id);
        if(userHasActiveLoans) {
            throw new UserCantBeDeletedHasLoanException("El usuario no puede ser borrado, tiene los siguiente libros: " + String.join(", ", bookTitles));
        }
        userRepository.delete(user);
    }


}
