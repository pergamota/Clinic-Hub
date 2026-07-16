package com.clinichub.mapper;

import com.clinichub.dto.AppointmentRequestDTO;
import com.clinichub.dto.AppointmentResponseDTO;
import com.clinichub.model.Appointment;

public class AppointmentMapper {
    
    public static Appointment toEntity(AppointmentRequestDTO dto) {

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(dto.appointmentDate());

        return appointment;
    }
    
    public static AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
        appointment.getId(),
        appointment.getPatient().getId(),
        appointment.getDoctor().getId(),    
        appointment.getAppointmentDate(),
        appointment.getStatus()
        );
    }

}
