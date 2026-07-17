package com.clinichub.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinichub.dto.AppointmentRequestDTO;
import com.clinichub.dto.AppointmentResponseDTO;
import com.clinichub.dto.AppointmentStatusUpdateDTO;
import com.clinichub.exception.BusinessRuleException;
import com.clinichub.exception.ResourceNotFoundException;
import com.clinichub.mapper.AppointmentMapper;
import com.clinichub.model.Appointment;
import com.clinichub.model.Doctor;
import com.clinichub.model.Patient;
import com.clinichub.repository.AppointmentRepository;
import com.clinichub.repository.DoctorRepository;
import com.clinichub.repository.PatientRepository;

@Service
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository; 
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService (AppointmentRepository appointmentRepository,
        DoctorRepository doctorRepository, PatientRepository patientRepository) {

        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;

    }

    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        if (dto.appointmentDate().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Appointments cannot be scheduled in the past");
        }

        Doctor doctor = doctorRepository.findById(dto.doctorId())
        .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Patient patient = patientRepository.findById(dto.patientId())
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Appointment appointment = AppointmentMapper.toEntity(dto);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentMapper.toResponseDTO(saved);
    }

    public AppointmentResponseDTO getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        return AppointmentMapper.toResponseDTO(appointment);
    }

    public List<AppointmentResponseDTO> getAll() {
        return appointmentRepository.findAll()
        .stream()
        .map(AppointmentMapper::toResponseDTO)
        .toList();
    }

    public AppointmentResponseDTO update(Long id, AppointmentStatusUpdateDTO dto) {
        Appointment appointment = appointmentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointment.setStatus(dto.status());

        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        appointmentRepository.delete(appointment);
    }




}
