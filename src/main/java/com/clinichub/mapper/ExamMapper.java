package com.clinichub.mapper;

import com.clinichub.dto.ExamRequestDTO;
import com.clinichub.dto.ExamResponseDTO;
import com.clinichub.model.Exam;

public class ExamMapper {
    
    public static Exam toEntity(ExamRequestDTO dto) {

        Exam exam = new Exam();
        exam.setExamType(dto.examType());

        return exam;
    }
    
    public static ExamResponseDTO toResponseDTO(Exam exam) {
        return new ExamResponseDTO(
        exam.getId(),
        exam.getAppointment().getId(),
        exam.getExamType(),    
        exam.getResultUrl(),
        exam.getRequestedAt()
        );
    }

}
