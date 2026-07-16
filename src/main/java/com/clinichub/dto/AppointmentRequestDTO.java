package com.clinichub.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record AppointmentRequestDTO (
    @NotNull(message = "The Patient id is required") Long patientId,
    @NotNull(message = "The Patient id is required") Long doctorId,
    LocalDateTime appointmentDate
) {
    
}
