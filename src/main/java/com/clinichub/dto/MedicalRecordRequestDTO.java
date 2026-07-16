package com.clinichub.dto;

import jakarta.validation.constraints.NotNull;

public record MedicalRecordRequestDTO(
    @NotNull(message = "The Patient id is required") Long appointmentId,
    String description) {
    
}
