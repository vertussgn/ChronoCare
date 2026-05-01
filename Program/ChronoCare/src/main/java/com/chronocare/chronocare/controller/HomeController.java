package com.chronocare.chronocare.controller;

import com.chronocare.chronocare.model.Doctor;
import com.chronocare.chronocare.model.Patient;
import com.chronocare.chronocare.repository.DoctorRepository;
import com.chronocare.chronocare.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // ✅ BCrypt jelszókódoló – SecurityConfig.java definiálja
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Főoldal
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Belépési oldal
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Páciens regisztrációs oldal – orvosok listájával
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("doctors", doctorRepository.findAll());
        return "register";
    }

    // ✅ Regisztráció feldolgozása – jelszó BCrypt-tel titkosítva kerül az adatbázisba
    @PostMapping("/register")
    public String registerPatient(Patient patient, @RequestParam Long doctorId) {
        if (patient.getDiagnosis() == null || patient.getDiagnosis().isBlank()) {
            patient.setDiagnosis("Kivizsgálás alatt");
        }
        Doctor doc = doctorRepository.findById(doctorId).orElseThrow();
        patient.setDoctor(doc);

        // ✅ KRITIKUS JAVÍTÁS: jelszó titkosítása – nyílt szöveg SOHA nem kerül az adatbázisba
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));

        patientRepository.save(patient);
        return "redirect:/login?registered";
    }

    // ✅ Belépés feldolgozása – BCrypt matches() hash összehasonlítással (nem equals()!)
    @PostMapping("/perform_login")
    public String performLogin(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String role,
                               HttpSession session,
                               Model model) {

        if (role.equals("DOCTOR")) {
            Optional<Doctor> doc = doctorRepository.findByUsername(username);
            // ✅ passwordEncoder.matches(beírt jelszó, tárolt hash) – biztonságos összehasonlítás
            if (doc.isPresent() && passwordEncoder.matches(password, doc.get().getPassword())) {
                session.setAttribute("user", doc.get());
                session.setAttribute("role", "DOCTOR");
                return "redirect:/doctor/home";
            }
        } else {
            Optional<Patient> pat = patientRepository.findByUsername(username);
            if (pat.isPresent() && passwordEncoder.matches(password, pat.get().getPassword())) {
                session.setAttribute("user", pat.get());
                session.setAttribute("role", "PATIENT");
                return "redirect:/patient/dashboard/" + pat.get().getId();
            }
        }

        model.addAttribute("error", "Hibás felhasználónév vagy jelszó!");
        return "login";
    }

    // Orvosi pácienslista – csak DOCTOR szerepkörrel érhető el
    @GetMapping("/doctor/home")
    public String doctorHome(Model model, HttpSession session) {
        Doctor currentDoctor = (Doctor) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (currentDoctor == null || !"DOCTOR".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("doctor", currentDoctor);
        model.addAttribute("patients", patientRepository.findByDoctor(currentDoctor));
        return "doctor_list";
    }

    // Páciens saját adatlapja – csak PATIENT szerepkörrel, csak saját ID-val
    @GetMapping("/patient/dashboard/{patientId}")
    public String patientDashboard(@PathVariable Long patientId,
                                   Model model,
                                   HttpSession session) {
        String role = (String) session.getAttribute("role");
        Patient sessionPatient = (Patient) session.getAttribute("user");

        // Szerepkör és azonosító ellenőrzése – más páciens adatai nem elérhetők
        if (sessionPatient == null || !"PATIENT".equals(role)
                || !sessionPatient.getId().equals(patientId)) {
            return "redirect:/login";
        }

        Patient freshPatient = patientRepository.findById(patientId).orElseThrow();

        model.addAttribute("patient", freshPatient);
        model.addAttribute("measurements", freshPatient.getMeasurements());
        return "patient_dashboard";
    }

    // Kijelentkezés
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}