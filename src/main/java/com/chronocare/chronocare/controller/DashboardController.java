package com.chronocare.chronocare.controller;

import com.chronocare.chronocare.model.Patient;
import com.chronocare.chronocare.repository.PatientRepository;
import com.chronocare.chronocare.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MonitoringService monitoringService;

    // Orvosi felület – páciens adatlap megjelenítése
    @GetMapping("/doctor/dashboard/{patientId}")
    public String showPatientData(@PathVariable Long patientId, Model model) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nincs ilyen páciens ID: " + patientId));

        model.addAttribute("patient", patient);
        model.addAttribute("measurements", patient.getMeasurements());

        return "dashboard";
    }

    // Manuális szimulációs gomb kezelése a dashboardon
    @PostMapping("/simulate/measurement")
    public String simulateMeasurement(@RequestParam Long patientId,
                                      @RequestParam String type,
                                      @RequestParam double value) {
        Patient patient = patientRepository.findById(patientId).orElseThrow();

        // Az életkorfüggő checkIfCritical a MonitoringService-ben fut
        monitoringService.recordMeasurement(patient, type, value);

        return "redirect:/doctor/dashboard/" + patientId;
    }
}