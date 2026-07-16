package com.clinichub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoctorRequestDTO(
    @NotNull(message = "The User id is required") Long userId, 
    @NotBlank(message = "The CRM is required") String crm,
    @NotNull(message = "The specialty id is required") Long specialtyId
) 
{
    
}
