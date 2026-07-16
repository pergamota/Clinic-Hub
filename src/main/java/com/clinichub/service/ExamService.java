package com.clinichub.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinichub.dto.ExamRequestDTO;
import com.clinichub.dto.ExamResponseDTO;
import com.clinichub.dto.ExamResultUpdateDTO;
import com.clinichub.mapper.ExamMapper;
import com.clinichub.model.Appointment;
import com.clinichub.model.Exam;
import com.clinichub.repository.AppointmentRepository;
import com.clinichub.repository.ExamRepository;

@Service
public class ExamService {
    
    private final ExamRepository examRepository;
    private final AppointmentRepository appointmentRepository;

    public ExamService(ExamRepository examRepository, 
        AppointmentRepository appointmentRepository) {
        this.examRepository = examRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public ExamResponseDTO create(ExamRequestDTO dto) {
        Appointment appointment = appointmentRepository.findById(dto.appointmentId())
        .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Exam exam = ExamMapper.toEntity(dto);
        exam.setAppointment(appointment);
        exam.setRequestedAt(LocalDateTime.now());

        Exam saved = examRepository.save(exam);
        return ExamMapper.toResponseDTO(saved);
    }

    public ExamResponseDTO getById(Long id) {
        Exam exam = examRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Exam not found"));

        return ExamMapper.toResponseDTO(exam);
    }

    public List<ExamResponseDTO> getAll() {
        return examRepository.findAll()
        .stream()
        .map(ExamMapper::toResponseDTO)
        .toList();
    }

    public ExamResponseDTO update(Long id, ExamResultUpdateDTO dto) {
        Exam exam = examRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Exam not found"));

        exam.setResultUrl(dto.resultUrl());

        Exam updated = examRepository.save(exam);
        return ExamMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        Exam exam = examRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Exam not found"));

        examRepository.delete(exam);
    }

}
