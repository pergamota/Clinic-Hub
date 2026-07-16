package com.clinichub.dto;

public record DoctorResponseDTO(Long id, Long userId,
    String crm, Long specialtyId
) {
    
}
