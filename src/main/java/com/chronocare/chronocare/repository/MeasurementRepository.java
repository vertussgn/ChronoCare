package com.chronocare.chronocare.repository;

import com.chronocare.chronocare.model.HealthMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MeasurementRepository extends JpaRepository<HealthMeasurement, Long> {

    //Egy adott páciens összes mérése
    List<HealthMeasurement> findByPatientId(Long patientId);
}