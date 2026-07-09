package com.clinichub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinichub.model.MedicalRecord;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

}
