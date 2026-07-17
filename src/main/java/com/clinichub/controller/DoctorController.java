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

import com.clinichub.dto.DoctorRequestDTO;
import com.clinichub.dto.DoctorResponseDTO;
import com.clinichub.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorResponseDTO> create
        (@Valid @RequestBody DoctorRequestDTO dto) 
    {
        return ResponseEntity.status(201).body
        (doctorService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getById
        (@PathVariable Long id) 
    {
        return ResponseEntity.ok(doctorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAll() {
        return ResponseEntity.ok(doctorService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> update
        (@PathVariable Long id,
        @Valid @RequestBody DoctorRequestDTO dto) {

        return ResponseEntity.ok(doctorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
