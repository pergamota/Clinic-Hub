package com.clinichub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinichub.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

}
 