package com.chronocare.chronocare.repository;

import com.chronocare.chronocare.model.Doctor;
import com.chronocare.chronocare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    java.util.Optional<Patient> findByUsername(String username);

    //Páciens keresése (orvos alapján)
    List<Patient> findByDoctor(Doctor doctor);
}