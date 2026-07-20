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

import com.clinichub.dto.SpecialtyRequestDTO;
import com.clinichub.dto.SpecialtyResponseDTO;
import com.clinichub.service.SpecialtyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SpecialtyResponseDTO> create(@Valid @RequestBody SpecialtyRequestDTO dto) {
        return ResponseEntity.status(201).body(specialtyService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> getById
        (@PathVariable Long id) 
    {
        return ResponseEntity.ok(specialtyService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDTO>> getAll() {
        return ResponseEntity.ok(specialtyService.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecialtyResponseDTO> update
        (@PathVariable Long id,
        @Valid @RequestBody SpecialtyRequestDTO dto) {

        return ResponseEntity.ok(specialtyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specialtyService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
