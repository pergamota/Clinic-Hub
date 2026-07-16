package com.clinichub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinichub.model.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    Optional <Exam> findByAppointmentId(Long id);

}
