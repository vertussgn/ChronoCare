package com.chronocare.chronocare.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private String diagnosis;


    private String username;
    private String password;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // Egy pácienshez több mérés tartozik
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<HealthMeasurement> measurements;
}