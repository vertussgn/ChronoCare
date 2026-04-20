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

        // Életkorfüggő riasztási logika
        measurement.setCritical(checkIfCritical(patient, type, value));

        measurementRepository.save(measurement);
    }

    // Küszöbértékek vizsgálata – életkor figyelembevételével
    private boolean checkIfCritical(Patient patient, String type, double value) {
        int age = patient.getAge();
        switch (type) {
            case "Vérnyomás":
                double maxSystolic = getMaxSystolicByAge(age);
                double minSystolic = getMinSystolicByAge(age);
                return value > maxSystolic || value < minSystolic;
            case "Pulzus":
                // Standard orvosi tartomány: 60-100 BPM
                return value > 100 || value < 60;
            case "Testhő":
                // Lázas állapot
                return value > 38.0 || value < 36.0;
            case "Véroxigén":
                // Hipoxia gyanúja 90% alatt
                return value < 90.0;
            case "Vércukor":
                // 7.8 felett vagy 3.9 alatt kritikus
                return value > 7.8 || value < 3.9;
            default:
                return false;
        }
    }

    // Dinamikus szisztolés maximum életkor alapján
    private double getMaxSystolicByAge(int age) {
        if (age < 18) return 120.0;
        else if (age < 65) return 140.0;
        else return 150.0;
    }

    // Dinamikus szisztolés minimum életkor alapján
    private double getMinSystolicByAge(int age) {
        if (age < 18) return 90.0;
        else return 100.0;
    }
}