package com.clinichub.mapper;

import com.clinichub.dto.UserRequestDTO;
import com.clinichub.dto.UserResponseDTO;
import com.clinichub.model.User;

public class UserMapper {

    public static User toEntity(UserRequestDTO dto) {

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        return user;

    }

    public static UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
        user.getId(), 
        user.getName(), 
        user.getEmail(), 
        user.getRole());

    }
    
}
