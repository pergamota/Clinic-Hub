package com.clinichub.mapper;

import com.clinichub.dto.PatientRequestDTO;
import com.clinichub.dto.PatientResponseDTO;
import com.clinichub.model.Patient;

public class PatientMapper {
    
    public static Patient toEntity(PatientRequestDTO dto) {

        Patient patient = new Patient();
        patient.setCpf(dto.cpf());
        patient.setBirthDate(dto.birthDate());
        patient.setPhone(dto.phone());
        return patient;

    }

    public static PatientResponseDTO toResponseDTO(Patient patient) {
            return new PatientResponseDTO(
            patient.getId(),
            patient.getUser().getId(),
            patient.getCpf(),
            patient.getBirthDate(),
            patient.getPhone()
        );
    }

}
