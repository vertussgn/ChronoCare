package com.chronocare.chronocare.controller;

import com.chronocare.chronocare.model.Patient;
import com.chronocare.chronocare.repository.PatientRepository;
import com.chronocare.chronocare.service.MonitoringService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MonitoringService monitoringService;

    // Orvosi felület – páciens adatlap megjelenítése
    // Csak DOCTOR szerepkörrel érhető el
    @GetMapping("/doctor/dashboard/{patientId}")
    public String showPatientData(@PathVariable Long patientId,
                                  Model model,
                                  HttpSession session) {
        String role = (String) session.getAttribute("role");
        if (role == null || !"DOCTOR".equals(role)) {
            return "redirect:/login";
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nincs ilyen páciens ID: " + patientId));

        model.addAttribute("patient", patient);
        model.addAttribute("measurements", patient.getMeasurements());

        return "dashboard";
    }

    @PostMapping("/simulate/measurement")
    public String simulateMeasurement(@RequestParam Long patientId,
                                      @RequestParam String type,
                                      @RequestParam double value,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute("role");
        if (role == null || !"DOCTOR".equals(role)) {
            return "redirect:/login";
        }

        Patient patient = patientRepository.findById(patientId).orElseThrow();
        boolean saved = monitoringService.recordMeasurement(patient, type, value);

        if (!saved) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Érvénytelen mérési adat: " + type + " = " + value + " – az adat nem került mentésre.");
        }

        return "redirect:/doctor/dashboard/" + patientId;
    }
}