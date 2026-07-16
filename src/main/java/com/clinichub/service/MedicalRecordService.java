package com.clinichub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinichub.dto.MedicalRecordRequestDTO;
import com.clinichub.dto.MedicalRecordResponseDTO;
import com.clinichub.dto.MedicalRecordUpdateDTO;
import com.clinichub.mapper.MedicalRecordMapper;
import com.clinichub.model.Appointment;
import com.clinichub.model.MedicalRecord;
import com.clinichub.repository.AppointmentRepository;
import com.clinichub.repository.MedicalRecordRepository;

@Service
public class MedicalRecordService {
    
    private final MedicalRecordRepository medicalRecordRepository; 
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, 
        AppointmentRepository appointmentRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public MedicalRecordResponseDTO create(MedicalRecordRequestDTO dto) {

        Appointment appointment = appointmentRepository.findById(dto.appointmentId())
        .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() != Appointment.Status.COMPLETED) {
            throw new RuntimeException
            ("Medical record can only be created for completed appointments");
        }

        MedicalRecord medicalRecord = MedicalRecordMapper.toEntity(dto);
        medicalRecord.setAppointment(appointment);

        MedicalRecord saved = medicalRecordRepository.save(medicalRecord);
        return MedicalRecordMapper.toResponseDTO(saved);
    }

    public MedicalRecordResponseDTO getById(Long id) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Medical Record not found"));

        return MedicalRecordMapper.toResponseDTO(medicalRecord);
    }

    public List<MedicalRecordResponseDTO> getAll() {
        return medicalRecordRepository.findAll()
        .stream()
        .map(MedicalRecordMapper::toResponseDTO)
        .toList();
    }

    public MedicalRecordResponseDTO update(Long id, MedicalRecordUpdateDTO dto) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Medical record not found"));

        medicalRecord.setDescription(dto.description());

        MedicalRecord updated = medicalRecordRepository.save(medicalRecord);
        return MedicalRecordMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("MedicalRecord not found"));

       medicalRecordRepository.delete(medicalRecord);
    }

}
