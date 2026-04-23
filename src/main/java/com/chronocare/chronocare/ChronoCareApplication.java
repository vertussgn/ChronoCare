package com.chronocare.chronocare;

import com.chronocare.chronocare.model.Doctor;
import com.chronocare.chronocare.model.Patient;
import com.chronocare.chronocare.repository.DoctorRepository;
import com.chronocare.chronocare.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ChronoCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChronoCareApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(PatientRepository patientRepo,
                                        DoctorRepository doctorRepo,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            if (doctorRepo.count() == 0) {

                //Orvosok
                Doctor d1 = new Doctor();
                d1.setName("Dr. House");
                d1.setUsername("house");
                d1.setPassword(passwordEncoder.encode("admin"));
                doctorRepo.save(d1);

                Doctor d2 = new Doctor();
                d2.setName("Dr. Strange");
                d2.setUsername("strange");
                d2.setPassword(passwordEncoder.encode("admin"));
                doctorRepo.save(d2);

                //Betegek
                Patient p1 = new Patient();
                p1.setName("Teszt János");
                p1.setAge(65);
                p1.setDiagnosis("Magas vérnyomás");
                p1.setTajNumber("123456789");
                p1.setUsername("janos");
                p1.setPassword(passwordEncoder.encode("1234"));
                p1.setDoctor(d1);
                patientRepo.save(p1);

                Patient p2 = new Patient();
                p2.setName("Minta Mária");
                p2.setAge(42);
                p2.setDiagnosis("Cukorbetegség");
                p2.setTajNumber("987654321");
                p2.setUsername("maria");
                p2.setPassword(passwordEncoder.encode("1234"));
                p2.setDoctor(d2);
                patientRepo.save(p2);

                System.out.println("--- ADATBÁZIS INICIALIZÁLVA (2 orvos, 2 beteg)");
            }
        };
    }
}