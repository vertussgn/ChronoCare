package com.chronocare.chronocare.service;

import com.chronocare.chronocare.model.Patient;
import com.chronocare.chronocare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MQTTListener {

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private PatientRepository patientRepository;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<String> message) {
        String payload = message.getPayload();
        System.out.println("MQTT adat érkezett: " + payload);

        try {
            org.json.JSONObject json = new org.json.JSONObject(payload);

            //  patientId kinyerése a topicból
            String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
            Long patientId = extractPatientIdFromTopic(topic);

            String type  = json.getString("type");
            double value = json.getDouble("value");

            // Típusnév normalizálása: angol → magyar
            type = normalizeType(type);

            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException(
                            "Beteg nem található ID: " + patientId
                                    + " (topic: " + topic + ")"));

            monitoringService.recordMeasurement(patient, type, value);

            System.out.println("✓ Mérés mentve: " + type + " = " + value
                    + " | beteg: " + patient.getName()
                    + " (ID: " + patientId + ", topic: " + topic + ")");

        } catch (Exception e) {
            System.out.println("✗ Hibás MQTT üzenet: " + e.getMessage()
                    + " | payload: " + payload);
        }
    }

    /**
     * A topic stringből kinyeri a patientId-t.
     *
     * Példák:
     *   "patient/1/measurements" → 1L
     *   "patient/2/measurements" → 2L
     *   "patient/42/measurements" → 42L
     *
     * @throws IllegalArgumentException ha a topic formátuma érvénytelen
     */
    private Long extractPatientIdFromTopic(String topic) {
        if (topic == null || topic.isEmpty()) {
            throw new IllegalArgumentException("MQTT topic null vagy üres");
        }
        // "patient/1/measurements" → ["patient", "1", "measurements"]
        String[] parts = topic.split("/");
        if (parts.length < 3 || !parts[0].equals("patient")) {
            throw new IllegalArgumentException(
                    "Érvénytelen topic formátum: " + topic
                            + " (elvárt: patient/{id}/measurements)");
        }
        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Érvénytelen patientId a topicban: '" + parts[1]
                            + "' (topic: " + topic + ")");
        }
    }

    /**
     * A Wokwi által küldött típusneveket normalizálja.
     *
     *   "blood_pressure"  →  "Vérnyomás"
     *   "heart_rate"      →  "Pulzus"
     *   "blood_sugar"     →  "Vércukor"
     *   "spo2"            →  "Véroxigén"
     *   "temperature"     →  "Testhő"
     */
    private String normalizeType(String raw) {
        String normalized = raw.toLowerCase().replaceAll("[\\s_\\-]", "");
        switch (normalized) {
            case "bloodpressure":
            case "vernyomas":
                return "Vérnyomás";
            case "heartrate":
            case "pulse":
            case "pulzus":
                return "Pulzus";
            case "bloodsugar":
            case "glucose":
            case "vercukor":
                return "Vércukor";
            case "spo2":
            case "oxygen":
            case "veroxigen":
                return "Véroxigén";
            case "temperature":
            case "testho":
                return "Testhő";
            default:
                System.out.println("⚠ Ismeretlen típus: '" + raw
                        + "' (normalized: '" + normalized + "')");
                return raw;
        }
    }
}