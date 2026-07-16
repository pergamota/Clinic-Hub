package com.clinichub.mapper;

import com.clinichub.dto.DoctorRequestDTO;
import com.clinichub.dto.DoctorResponseDTO;
import com.clinichub.model.Doctor;

public class DoctorMapper {
    
    public static Doctor toEntity(DoctorRequestDTO dto) {

        Doctor doctor = new Doctor();
        doctor.setCrm(dto.crm());

        return doctor;
    }
    
    public static DoctorResponseDTO toResponseDTO(Doctor doctor) {
            return new DoctorResponseDTO(
            doctor.getId(),
            doctor.getUser().getId(),
            doctor.getCrm(),
            doctor.getSpecialty().getId()
        );
    }

}
