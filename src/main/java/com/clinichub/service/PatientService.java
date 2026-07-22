package com.clinichub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinichub.dto.PatientRequestDTO;
import com.clinichub.dto.PatientResponseDTO;
import com.clinichub.exception.BusinessRuleException;
import com.clinichub.exception.ResourceNotFoundException;
import com.clinichub.mapper.PatientMapper;
import com.clinichub.model.Patient;
import com.clinichub.model.User;
import com.clinichub.repository.PatientRepository;
import com.clinichub.repository.UserRepository;

@Service
public class PatientService {
    
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    
    public PatientService(PatientRepository patientRepository, 
        UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }


    public PatientResponseDTO create(PatientRequestDTO dto) {
        if (patientRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new BusinessRuleException("This CPF is already registered");
        }

        User user = userRepository.findById(dto.userId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Patient patient = PatientMapper.toEntity(dto);
        patient.setUser(user);

        Patient saved = patientRepository.save(patient);
        return PatientMapper.toResponseDTO(saved);
    }

    public PatientResponseDTO getById(Long id) {
        Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return PatientMapper.toResponseDTO(patient);
    }

    public List<PatientResponseDTO> getAll() {
        return patientRepository.findAll()
        .stream()
        .map(PatientMapper::toResponseDTO)
        .toList();
    }

    public PatientResponseDTO update(Long id, PatientRequestDTO dto) {
        Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        
        patient.setPhone(dto.phone());
        patient.setBirthDate(dto.birthDate());
    
        Patient updated = patientRepository.save(patient);
        return PatientMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        Patient patient = patientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        patientRepository.delete(patient);
    }

}
