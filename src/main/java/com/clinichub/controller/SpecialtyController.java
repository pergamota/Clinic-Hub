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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Specialties", description = "Manage medical specialties")
@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @Operation(summary = "Create a new specialty", description = "Requires ADMIN role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Specialty created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Specialty name already exists")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SpecialtyResponseDTO> create(@Valid @RequestBody SpecialtyRequestDTO dto) {
        return ResponseEntity.status(201).body(specialtyService.create(dto));
    }

    @Operation(summary = "Get a specialty by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Specialty found"),
        @ApiResponse(responseCode = "404", description = "Specialty not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyResponseDTO> getById
        (@PathVariable Long id) 
    {
        return ResponseEntity.ok(specialtyService.getById(id));
    }

    @Operation(summary = "List all specialties")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List returned successfully")
    })
    @GetMapping
    public ResponseEntity<List<SpecialtyResponseDTO>> getAll() {
        return ResponseEntity.ok(specialtyService.getAll());
    }

    @Operation(summary = "Update an existing specialty", description = "Requires ADMIN role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Specialty updated successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "404", description = "Specialty not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecialtyResponseDTO> update
        (@PathVariable Long id,
        @Valid @RequestBody SpecialtyRequestDTO dto) {

        return ResponseEntity.ok(specialtyService.update(id, dto));
    }

    @Operation(summary = "Delete a specialty", description = "Requires ADMIN role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Specialty deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Specialty not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specialtyService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
