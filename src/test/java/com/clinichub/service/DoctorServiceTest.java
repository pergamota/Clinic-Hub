package com.clinichub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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

    @Test
    void shouldGetByIdSuccessfully() {
        User user = new User();
        user.setId(1L);

        Specialty specialty = new Specialty();
        specialty.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setCrm("1010");
        doctor.setUser(user);
        doctor.setSpecialty(specialty);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorResponseDTO result = doctorService.getById(1L);

        assertEquals(1L, result.id());
        assertEquals("1010", result.crm());
        assertEquals(1L, result.userId());
        assertEquals(1L, result.specialtyId());
    }

    @Test
    void shouldGetByIdNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getById(1L));
    }


    @Test
    void shouldGetAllSuccessfully() {

        User user1 = new User();
        user1.setId(1L);
        Specialty specialty1 = new Specialty();
        specialty1.setId(1L);

        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setCrm("1010");
        doctor1.setUser(user1);
        doctor1.setSpecialty(specialty1);

        User user2 = new User();
        user2.setId(2L);
        Specialty specialty2 = new Specialty();
        specialty2.setId(2L);

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setCrm("2020");
        doctor2.setUser(user2);
        doctor2.setSpecialty(specialty2);

        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));

        List<DoctorResponseDTO> result = doctorService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentDoctor() {

        DoctorRequestDTO dto = new DoctorRequestDTO(1L, "1010", 1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> doctorService.update(1L, dto));

    }

    @Test
    void shouldThrowExceptionWhenUpdatingDoctorWithInvalidSpecialty() {

        User user = new User();
        user.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setCrm("1010");
        doctor.setUser(user);

        DoctorRequestDTO dto = new DoctorRequestDTO(1L, "1010", 1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.update(1L, dto));

    }

    @Test
    void shouldThrowExceptionWhenUpdatingDoctorWithDuplicateCrm() {
        User user = new User();
        user.setId(1L);

        Specialty specialty = new Specialty();
        specialty.setId(1L);

        Doctor doctorBeingUpdated = new Doctor();
        doctorBeingUpdated.setId(1L);
        doctorBeingUpdated.setUser(user);

        Doctor anotherDoctorWithSameCrm = new Doctor();
        anotherDoctorWithSameCrm.setId(2L);
        anotherDoctorWithSameCrm.setCrm("1010");

        DoctorRequestDTO dto = new DoctorRequestDTO(1L, "1010", 1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctorBeingUpdated));
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(doctorRepository.findByCrm("1010")).thenReturn(Optional.of(anotherDoctorWithSameCrm));

        assertThrows(BusinessRuleException.class, () -> doctorService.update(1L, dto));
    }

    @Test
    void shouldUpdateDoctorSuccessfully() {
        User user = new User();
        user.setId(1L);

        Specialty specialty = new Specialty();
        specialty.setId(1L);

        Doctor doctorBeingUpdated = new Doctor();
        doctorBeingUpdated.setId(1L);
        doctorBeingUpdated.setUser(user);

        DoctorRequestDTO dto = new DoctorRequestDTO(1L, "1010", 1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctorBeingUpdated));
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(doctorRepository.findByCrm("1010")).thenReturn(Optional.empty());
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(inv -> inv.getArgument(0));

        DoctorResponseDTO result = doctorService.update(1L, dto);

        assertEquals("1010", result.crm());
        assertEquals(1L, result.specialtyId());
    }

    @Test
    void shouldDeleteDoctorSuccessfully() {
        User user = new User();
        user.setId(1L);

        Doctor doctorBeingDeleted = new Doctor();
        doctorBeingDeleted.setId(1L);
        doctorBeingDeleted.setUser(user);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctorBeingDeleted));

        doctorService.delete(1L);

        verify(doctorRepository).delete(doctorBeingDeleted);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentDoctor() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.delete(1L));
    }

}
