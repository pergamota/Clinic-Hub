package com.clinichub.dto;

import java.time.LocalDateTime;

public record ExamResponseDTO(Long id, Long appointmentId,
    String examType, String resultUrl, LocalDateTime requestedAt
) {
    
}
