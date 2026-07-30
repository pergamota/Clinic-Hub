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

import com.clinichub.dto.DoctorRequestDTO;
import com.clinichub.dto.DoctorResponseDTO;
import com.clinichub.service.DoctorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Doctors", description = "Manage doctors")
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Operation(summary = "Create a new doctor", description = "Requires ADMIN role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "User or Specialty not found"),
        @ApiResponse(responseCode = "409", description = "CRM already exists")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DoctorResponseDTO> create
        (@Valid @RequestBody DoctorRequestDTO dto) 
    {
        return ResponseEntity.status(201).body
        (doctorService.create(dto));
    }

    @Operation(summary = "Get a doctor by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor found"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getById
        (@PathVariable Long id) 
    {
        return ResponseEntity.ok(doctorService.getById(id));
    }

    @Operation(summary = "List all doctors")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List returned successfully")
    })
    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAll() {
        return ResponseEntity.ok(doctorService.getAll());
    }

    @Operation(summary = "Update an existing doctor", description = "Requires ADMIN role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Doctor or Specialty not found"),
        @ApiResponse(responseCode = "409", description = "CRM already exists")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> update
        (@PathVariable Long id,
        @Valid @RequestBody DoctorRequestDTO dto) {

        return ResponseEntity.ok(doctorService.update(id, dto));
    }

    @Operation(summary = "Delete a doctor", description = "Requires ADMIN role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
