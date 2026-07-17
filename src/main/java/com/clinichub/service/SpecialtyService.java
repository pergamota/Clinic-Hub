package com.clinichub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinichub.dto.SpecialtyRequestDTO;
import com.clinichub.dto.SpecialtyResponseDTO;
import com.clinichub.exception.BusinessRuleException;
import com.clinichub.exception.ResourceNotFoundException;
import com.clinichub.mapper.SpecialtyMapper;
import com.clinichub.model.Specialty;
import com.clinichub.repository.SpecialtyRepository;

@Service
public class SpecialtyService {
    
    private final SpecialtyRepository specialtyRepository;

    public SpecialtyService(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    public SpecialtyResponseDTO create(SpecialtyRequestDTO dto) {
        if (specialtyRepository.findByName(dto.name()).isPresent()) {
            throw new BusinessRuleException("Specialty name already exists");
        }

        Specialty specialty = SpecialtyMapper.toEntity(dto);
        Specialty saved = specialtyRepository.save(specialty);
        
        return SpecialtyMapper.toResponseDTO(saved);
    }

    public SpecialtyResponseDTO getById(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Specialty not found"));

        return SpecialtyMapper.toResponseDTO(specialty);
    }

    public List<SpecialtyResponseDTO> getAll() {
        return specialtyRepository.findAll()
        .stream()
        .map(SpecialtyMapper::toResponseDTO)
        .toList();
    }

    public SpecialtyResponseDTO update(Long id, SpecialtyRequestDTO dto) {
        Specialty specialty = specialtyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Specialty not found"));

        specialty.setName(dto.name());

        Specialty updated = specialtyRepository.save(specialty);
        return SpecialtyMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Specialty not found"));

        specialtyRepository.delete(specialty);
    }

}
