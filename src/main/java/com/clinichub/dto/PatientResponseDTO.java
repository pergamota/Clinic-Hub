package com.clinichub.dto;

import java.time.LocalDate;

public record PatientResponseDTO (Long id, Long userId,
    String cpf, LocalDate birthDate, String phone
){

}
