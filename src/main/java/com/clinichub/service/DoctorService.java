package com.clinichub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinichub.dto.DoctorRequestDTO;
import com.clinichub.dto.DoctorResponseDTO;
import com.clinichub.mapper.DoctorMapper;
import com.clinichub.model.Doctor;
import com.clinichub.model.Specialty;
import com.clinichub.model.User;
import com.clinichub.repository.DoctorRepository;
import com.clinichub.repository.SpecialtyRepository;
import com.clinichub.repository.UserRepository;

@Service
public class DoctorService {
    
    private final UserRepository userRepository; 
    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    public DoctorService(UserRepository userRepository, 
        DoctorRepository doctorRepository, 
        SpecialtyRepository specialtyRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
    }

    public DoctorResponseDTO create(DoctorRequestDTO dto) {
        if (doctorRepository.findByCrm(dto.crm()).isPresent()) {
            throw new RuntimeException("This CRM is already registered");
        }

        User user = userRepository.findById(dto.userId())
        .orElseThrow(() -> new RuntimeException("User not found"));

        Specialty specialty = specialtyRepository.findById(dto.specialtyId())
        .orElseThrow(() -> new RuntimeException("Specialty not found"));

        Doctor doctor = DoctorMapper.toEntity(dto);
        doctor.setUser(user);
        doctor.setSpecialty(specialty);

        Doctor saved = doctorRepository.save(doctor);
        return DoctorMapper.toResponseDTO(saved);
    }

    public DoctorResponseDTO getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return DoctorMapper.toResponseDTO(doctor);
    }

    public List<DoctorResponseDTO> getAll() {
        return doctorRepository.findAll()
        .stream()
        .map(DoctorMapper::toResponseDTO)
        .toList();
    }

    public DoctorResponseDTO update(Long id, DoctorRequestDTO dto) {
        Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Specialty specialty = specialtyRepository.findById(dto.specialtyId())
        .orElseThrow(() -> new RuntimeException("Specialty not found"));

        doctor.setSpecialty(specialty);

        Doctor updated = doctorRepository.save(doctor);
        return DoctorMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        Doctor doctor = doctorRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctorRepository.delete(doctor);
    }



}
