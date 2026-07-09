package com.clinichub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinichub.model.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

}
