package com.clinichub.dto;

import java.time.LocalDateTime;
import com.clinichub.model.Appointment.Status;

public record AppointmentResponseDTO(Long id, Long patientId,
    Long doctorId, LocalDateTime appointmentDate, Status status
) {
    
}
