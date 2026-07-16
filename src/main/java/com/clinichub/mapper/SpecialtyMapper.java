package com.clinichub.mapper;

import com.clinichub.dto.SpecialtyRequestDTO;
import com.clinichub.dto.SpecialtyResponseDTO;
import com.clinichub.model.Specialty;

public class SpecialtyMapper {

    public static Specialty toEntity(SpecialtyRequestDTO dto) {
        Specialty specialty = new Specialty();
        specialty.setName(dto.name());
        return specialty;
    }

    public static SpecialtyResponseDTO toResponseDTO(Specialty specialty) {
        return new SpecialtyResponseDTO(specialty.getId(), specialty.getName());
    }
}