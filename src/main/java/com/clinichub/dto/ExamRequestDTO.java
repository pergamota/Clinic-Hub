package com.clinichub.dto;

import jakarta.validation.constraints.NotNull;

public record ExamRequestDTO(
    @NotNull(message = "The Appointment id is required") Long appointmentId,
    String examType
) {}