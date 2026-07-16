package com.clinichub.repository;

import java.io.ObjectInputFilter.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.clinichub.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    Optional <Appointment> findByStatus(Status status);
}
 