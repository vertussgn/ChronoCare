package com.chronocare.chronocare.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class HealthMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;          // vérnyomás
    private double measuredValue;
    private LocalDateTime timestamp;

    private boolean isCritical;   // Riasztási státusz (MonitoringService)

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
}