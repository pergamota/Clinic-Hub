package com.clinichub.dto;

import java.time.LocalDateTime;

public record MedicalRecordResponseDTO(Long id,
    Long appointmentID,
    String description, 
    LocalDateTime createdAt
) {
    
}
