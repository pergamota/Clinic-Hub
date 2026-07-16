package com.clinichub.dto;

import com.clinichub.model.Appointment.Status;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateDTO(
    @NotNull(message = "The status is required") Status status
) {}