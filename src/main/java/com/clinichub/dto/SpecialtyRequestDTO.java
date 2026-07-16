package com.clinichub.dto;

import jakarta.validation.constraints.NotBlank;

public record SpecialtyRequestDTO(
    @NotBlank(message = "Specialty name is required")
    String name
) {
    
}
