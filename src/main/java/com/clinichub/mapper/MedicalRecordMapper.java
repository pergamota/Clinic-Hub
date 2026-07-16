package com.clinichub.mapper;

import com.clinichub.dto.MedicalRecordRequestDTO;
import com.clinichub.dto.MedicalRecordResponseDTO;
import com.clinichub.model.MedicalRecord;

public class MedicalRecordMapper {
    
    public static MedicalRecord toEntity(MedicalRecordRequestDTO dto) {

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setDescription(dto.description());

        return medicalRecord;
    }
    
    public static MedicalRecordResponseDTO toResponseDTO(MedicalRecord medicalRecord) {
        return new MedicalRecordResponseDTO(
        medicalRecord.getId(),
        medicalRecord.getAppointment().getId(),
        medicalRecord.getDescription(),    
        medicalRecord.getCreatedAt()
        );
    }

}
