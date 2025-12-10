package com.chronocare.chronocare.service;

import com.chronocare.chronocare.model.HealthMeasurement;
import com.chronocare.chronocare.model.Patient;
import com.chronocare.chronocare.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MonitoringService {

    @Autowired
    private MeasurementRepository measurementRepository;

    // Új mérés rögzítése és ellenőrzése
    public void recordMeasurement(Patient patient, String type, double value) {
        HealthMeasurement measurement = new HealthMeasurement();
        measurement.setPatient(patient);
        measurement.setType(type);
        measurement.setMeasuredValue(value);
        measurement.setTimestamp(LocalDateTime.now());

        // Riasztási logika
        measurement.setCritical(checkIfCritical(type, value));

        measurementRepository.save(measurement);
    }

    // Küszöbértékek vizsgálata
    private boolean checkIfCritical(String type, double value) {
        if (type.equals("Vérnyomás")) {
            // Pl. 140 felett vagy 90 alatt baj van
            return value > 140.0 || value < 90.0;
        } else if (type.equals("Vércukor")) {
            // Pl. 7.8 felett vagy 3.9 alatt baj van
            return value > 7.8 || value < 3.9;
        }
        return false;
    }
}