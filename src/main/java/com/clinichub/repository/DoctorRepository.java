package com.clinichub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinichub.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional <Doctor> findByCrm(String crm);

}