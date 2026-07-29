package com.clinichub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinichub.dto.AppointmentRequestDTO;
import com.clinichub.dto.AppointmentResponseDTO;
import com.clinichub.dto.AppointmentStatusUpdateDTO;
import com.clinichub.exception.BusinessRuleException;
import com.clinichub.exception.ResourceNotFoundException;
import com.clinichub.model.Appointment;
import com.clinichub.model.Doctor;
import com.clinichub.model.Patient;
import com.clinichub.model.Appointment.Status;
import com.clinichub.repository.AppointmentRepository;
import com.clinichub.repository.DoctorRepository;
import com.clinichub.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;


    @Test
    void shouldThrowExceptionWhenAppointmentDateIsInThePast() {
        AppointmentRequestDTO dto = new AppointmentRequestDTO
        (1L, 2L, LocalDateTime.now().minusDays(1));

        assertThrows(BusinessRuleException.class, () -> appointmentService.create(dto));
    }

    @Test
    void shouldThrowExceptionWhenDoctorNotFound() {
       AppointmentRequestDTO dto = new AppointmentRequestDTO
        (1L, 2L, LocalDateTime.now().plusDays(1)); 

        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(dto));
    }

    @Test
    void shouldThrowExceptionWhenPatientNotFound() {
        AppointmentRequestDTO dto = new AppointmentRequestDTO(1L, 1L, LocalDateTime.now().plusDays(1));

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(dto));
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {   

        Doctor doctor = new Doctor();
        doctor.setId(2L);

        Patient patient = new Patient();
        patient.setId(1L);

        AppointmentRequestDTO dto = new AppointmentRequestDTO
        (1L, 2L, LocalDateTime.now().plusDays(1));

        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        AppointmentResponseDTO result = appointmentService.create(dto);

        assertEquals(1L, result.patientId());
        assertEquals(2L, result.doctorId());
        assertEquals(dto.appointmentDate(), result.appointmentDate());
        assertEquals(Appointment.Status.SCHEDULED, result.status());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentAppointment() {
        AppointmentStatusUpdateDTO dto = new AppointmentStatusUpdateDTO(Status.SCHEDULED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.update(1L, dto));
    }

    @Test
    void shouldUpdateAppointmentStatusSuccessfully() {

        Patient patient = new Patient();
        patient.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Status status = Status.SCHEDULED;

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(status);

        AppointmentStatusUpdateDTO dto = new AppointmentStatusUpdateDTO(Status.COMPLETED);
 
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        AppointmentResponseDTO result = appointmentService.update(1L, dto);

        assertEquals(1L, result.doctorId());
        assertEquals(1L, result.patientId());
        assertEquals(Appointment.Status.COMPLETED, result.status());
    }

    @Test
    void shouldGetByIdSuccessfully() {
        Patient patient = new Patient();
        patient.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Status status = Status.SCHEDULED;

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(status);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentResponseDTO result = appointmentService.getById(1L);

        assertEquals(1L, result.id());
        assertEquals(1L, result.patientId());
        assertEquals(1L, result.doctorId());
        assertEquals(Appointment.Status.SCHEDULED, result.status());
    }

    @Test
    void shouldGetByIdNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.getById(1L));
    }

    @Test
    void shouldGetAllSuccessfully() {
        Patient patient1 = new Patient();
        patient1.setId(1L);

        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);

        Status status1 = Status.SCHEDULED;

        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        appointment1.setPatient(patient1);
        appointment1.setDoctor(doctor1);
        appointment1.setStatus(status1);

        Patient patient2 = new Patient();
        patient2.setId(2L);

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);

        Status status2 = Status.SCHEDULED;

        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setPatient(patient2);
        appointment2.setDoctor(doctor2);
        appointment2.setStatus(status2);

        when(appointmentRepository.findAll()).thenReturn(List.of(appointment1, appointment2));

        List<AppointmentResponseDTO> result = appointmentService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldDeleteAppointmentSuccessfully() {
        Patient patient = new Patient();
        patient.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Status status = Status.SCHEDULED;

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(status);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.delete(1L);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.delete(1L));
    }

}
