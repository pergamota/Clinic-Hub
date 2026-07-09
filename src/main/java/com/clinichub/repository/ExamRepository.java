package com.clinichub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinichub.model.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {

}
