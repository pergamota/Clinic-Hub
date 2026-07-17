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

import com.clinichub.dto.AppointmentRequestDTO;
import com.clinichub.dto.AppointmentResponseDTO;
import com.clinichub.dto.AppointmentStatusUpdateDTO;
import com.clinichub.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> create
        (@Valid @RequestBody AppointmentRequestDTO dto) 
    {
        return ResponseEntity.status(201).body
        (appointmentService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getById
        (@PathVariable Long id) 
    {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAll() {
        return ResponseEntity.ok(appointmentService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> update
        (@PathVariable Long id,
        @Valid @RequestBody AppointmentStatusUpdateDTO dto) {

        return ResponseEntity.ok(appointmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
