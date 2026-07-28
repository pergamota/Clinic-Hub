package com.clinichub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinichub.dto.DoctorRequestDTO;
import com.clinichub.dto.DoctorResponseDTO;
import com.clinichub.exception.BusinessRuleException;
import com.clinichub.exception.ResourceNotFoundException;
import com.clinichub.model.Doctor;
import com.clinichub.repository.DoctorRepository;
import com.clinichub.repository.SpecialtyRepository;
import com.clinichub.repository.UserRepository;
import com.clinichub.model.User;
import com.clinichub.model.Specialty;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {
    
    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private DoctorService doctorService;


    @Test
    void shouldThrowExceptionDoctorCrmAlreadyExists() {

        DoctorRequestDTO dto = new DoctorRequestDTO(1L, "1010", 1L);

        when(doctorRepository.findByCrm("1010")).thenReturn(Optional.of(new Doctor()));

        assertThrows(BusinessRuleException.class, () -> doctorService.create(dto));

    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        DoctorRequestDTO dto = new DoctorRequestDTO(1L, "1010", 1L);

        when(doctorRepository.findByCrm("1010")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.create(dto));

    }

    @Test
    void shouldThrowExceptionWhenSpecialtyNotFound() {

        DoctorRequestDTO dto = new DoctorRequestDTO(1L, "1010", 1L);

        when(doctorRepository.findByCrm("1010")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.create(dto));

    }

    @Test
    void shouldCreateDoctorSuccessfully() {

        DoctorRequestDTO dto = new DoctorRequestDTO(1L, "1010", 1L);

        User user = new User();
        user.setId(1L);

        Specialty specialty = new Specialty();
        specialty.setId(1L);

        when(doctorRepository.findByCrm("1010")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(inv -> inv.getArgument(0));


        DoctorResponseDTO result = doctorService.create(dto);

        assertEquals("1010", result.crm());
        assertEquals(1L, result.userId()); 
        assertEquals(1L, result.specialtyId());

    }

}
