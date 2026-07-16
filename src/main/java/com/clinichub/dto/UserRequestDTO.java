package com.clinichub.dto;
import com.clinichub.model.User.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(String name,
    @NotBlank (message = "The email is required") 
    @Email(message = "Email must be valid") String email, 
    @NotBlank (message = "The password is required") String password,
    Role role
) {
    

}
