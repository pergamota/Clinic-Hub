package com.clinichub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinichub.dto.MedicalRecordRequestDTO;
import com.clinichub.dto.MedicalRecordResponseDTO;
import com.clinichub.dto.MedicalRecordUpdateDTO;
import com.clinichub.service.MedicalRecordService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {
    
    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO> create
        (@Valid @RequestBody MedicalRecordRequestDTO dto) 
    {
        return ResponseEntity.status(201).body
        (medicalRecordService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> getById
        (@PathVariable Long id) 
    {
        return ResponseEntity.ok(medicalRecordService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponseDTO>> getAll() {
        return ResponseEntity.ok(medicalRecordService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> update
        (@PathVariable Long id,
        @Valid @RequestBody MedicalRecordUpdateDTO dto) {

        return ResponseEntity.ok(medicalRecordService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicalRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
