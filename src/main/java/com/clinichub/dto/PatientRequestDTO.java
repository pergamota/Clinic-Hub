package com.clinichub.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PatientRequestDTO (
    @NotNull(message = "The user id is required") Long userId,
    @NotBlank (message = "The cpf is required") String cpf, 
    LocalDate birthDate, String phone
) {
    
}
