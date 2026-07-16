package com.clinichub.dto;

import jakarta.validation.constraints.NotBlank;

public record MedicalRecordUpdateDTO(
    @NotBlank(message = "The description is required") String description
) {}