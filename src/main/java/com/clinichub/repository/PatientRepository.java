package com.clinichub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinichub.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    Optional <Patient> findByCpf(String cpf);

}
