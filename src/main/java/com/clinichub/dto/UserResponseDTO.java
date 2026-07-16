package com.clinichub.dto;
import com.clinichub.model.User.Role;

public record UserResponseDTO(Long id, String name,
    String email, Role role
) {
    
}
