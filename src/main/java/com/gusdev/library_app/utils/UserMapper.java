package com.gusdev.library_app.utils;

import com.gusdev.library_app.dtoResponse.UserResponseDTO;
import com.gusdev.library_app.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserResponseDTO toDTO(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getIsAdmin());
    }
    public static List<UserResponseDTO> toDTOList(List<User> usersList) {
        return usersList.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());

    }
}
