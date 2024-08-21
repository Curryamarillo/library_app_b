package com.gusdev.library_app.utils;

import com.gusdev.library_app.dtoResponse.UserDTO;
import com.gusdev.library_app.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDTO toDTO(User user){
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getIsAdmin());
    }
    public static List<UserDTO> toDTOList(List<User> usersList) {
        return usersList.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());

    }
}
