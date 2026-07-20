package com.clinichub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinichub.dto.ExamRequestDTO;
import com.clinichub.dto.ExamResponseDTO;
import com.clinichub.dto.ExamResultUpdateDTO;
import com.clinichub.service.ExamService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/exams")
public class ExamController {
    
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')") 
    @PostMapping
    public ResponseEntity<ExamResponseDTO> create
        (@Valid @RequestBody ExamRequestDTO dto) 
    {
        return ResponseEntity.status(201).body
        (examService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> getById
        (@PathVariable Long id) 
    {
        return ResponseEntity.ok(examService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ExamResponseDTO>> getAll() {
        return ResponseEntity.ok(examService.getAll());
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')") 
    @PutMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> update
        (@PathVariable Long id,
        @Valid @RequestBody ExamResultUpdateDTO dto) {

        return ResponseEntity.ok(examService.update(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
