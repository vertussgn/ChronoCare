/*
 * ChronoCare – beteg szimulátor (1 potméter)
 * TLS titkosított MQTT kapcsolat (port 8883, ssl://)
 */
#include <WiFi.h>
#include <WiFiClientSecure.h>        
#include <PubSubClient.h>
#include <ArduinoJson.h>

// ─────────────────────────────────────────────────────────────
const char* WIFI_SSID     = "Wokwi-GUEST";
const char* WIFI_PASSWORD = "";
const char* MQTT_BROKER   = "broker.hivemq.com";
const int   MQTT_PORT     = 8883;               //TLS port (volt: 1883)

// Betegek
const int PATIENT_IDS[]   = {8, 9};  // Melyik beteg (ID alapján)
const int PATIENT_COUNT   = 2;       // FIX: 2 beteg, nem 1

const int POT_PIN         = 34;

// Mit akarunk mérni?
const char* TYPES[] = {
  "blood_pressure",
  "heart_rate",
  "blood_sugar",
  "spo2",
  "temperature"
};
const int TYPE_COUNT = 5;  // Mennyi típust mérünk

const long SEND_INTERVAL = 3000;  // ms

// ─────────────────────────────────────────────────────────────
// TLS kliens (volt: WiFiClient espClient)
WiFiClientSecure espClient;
PubSubClient     mqttClient(espClient);
String           clientId;
unsigned long    lastSendTime = 0;

int cycleIndex = 0;

// ─────────────────────────────────────────────────────────────
void connectWifi() {
  Serial.print("WiFi csatlakozás...");
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  int tries = 0;
  while (WiFi.status() != WL_CONNECTED && tries++ < 20) {
    delay(500); Serial.print(".");
  }
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nWiFi OK – " + WiFi.localIP().toString());
  } else {
    Serial.println("\nWiFi sikertelen – újraindítás");
    delay(2000); ESP.restart();
  }
}

void connectMqtt() {
  while (!mqttClient.connected()) {
    Serial.print("MQTT TLS csatlakozás... ");
    if (mqttClient.connect(clientId.c_str())) {
      Serial.println("OK! (TLS titkosítva ✓)");
    } else {
      Serial.println("hiba rc=" + String(mqttClient.state()) + " – 5mp múlva");
      delay(5000);
    }
  }
}

// ─────────────────────────────────────────────────────────────
// A patientId a TOPICBAN van – MQTTListener innen olvassa
// ─────────────────────────────────────────────────────────────
void sendMeasurement(int patientId, const char* type, float value) {
  String topic = "patient/" + String(patientId) + "/measurements";

  // JSON-ban csak type és value
  StaticJsonDocument<128> doc;
  doc["type"]  = type;
  doc["value"] = value;

  char buffer[256];
  serializeJson(doc, buffer);

  bool ok = mqttClient.publish(topic.c_str(), buffer);

  Serial.println("─────────────────────────────────────");
  Serial.println("Beteg ID: " + String(patientId));
  Serial.println("Topic:    " + topic);
  Serial.println("Payload:  " + String(buffer));
  Serial.println(ok ? "Küldés: SIKERES ✓ (TLS)" : "Küldés: SIKERTELEN ✗");
}

// ─────────────────────────────────────────────────────────────
void setup() {
  Serial.begin(115200);
  delay(500);
  clientId = "ESP32-ChronoCare-" + String(millis());

  connectWifi();

  //TLS beállítása
  espClient.setInsecure();

  mqttClient.setServer(MQTT_BROKER, MQTT_PORT);
  mqttClient.setKeepAlive(60);
  mqttClient.setBufferSize(512);

  Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
  Serial.println("ChronoCare – IoT szimulátor (TLS/SSL)");
  Serial.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
}

// ─────────────────────────────────────────────────────────────
void loop() {
  if (!mqttClient.connected()) connectMqtt();
  mqttClient.loop();

  if (millis() - lastSendTime >= SEND_INTERVAL) {
    lastSendTime = millis();

    // FIX: PATIENT_COUNT = 2, ezért mindkét beteg kap adatot
    int patientIndex = cycleIndex % PATIENT_COUNT;
    int patientId    = PATIENT_IDS[patientIndex];
    const char* type = TYPES[cycleIndex / PATIENT_COUNT];

    int raw = analogRead(POT_PIN);
    float value;

      if (String(type) == "blood_pressure") {
      value = (float)map(raw, 0, 4095, 100, 140);
      Serial.println("\n[Ciklus " + String(cycleIndex) + "] Vérnyomás – Beteg: " + String(patientId));

      if (value > 140.0 || value < 100.0)
        Serial.println("⚠ Várható riasztás a szerveren!");
      else
        Serial.println("✓ Normál tartomány (100–140 Hgmm)");

    } else if (String(type) == "heart_rate") {
      value = (float)map(raw, 0, 4095, 60, 100);
      Serial.println("\n[Ciklus " + String(cycleIndex) + "] Pulzus – Beteg: " + String(patientId));
      if (value > 100.0 || value < 60.0)
        Serial.println("⚠ Várható riasztás a szerveren!");
      else
        Serial.println("✓ Normál tartomány (60–100 BPM)");

    } else if (String(type) == "blood_sugar") {
      value = (float)map(raw, 0, 4095, 39, 78) / 10.0f;
      Serial.println("\n[Ciklus " + String(cycleIndex) + "] Vércukor – Beteg: " + String(patientId));
      if (value < 3.9 || value > 7.8)
        Serial.println("⚠ Várható riasztás a szerveren!");
      else
        Serial.println("✓ Normál tartomány (3,9–7,8 mmol/l)");

    } else if (String(type) == "spo2") {
      value = (float)map(raw, 0, 4095, 90, 100);
      Serial.println("\n[Ciklus " + String(cycleIndex) + "] Véroxigén (SpO2) – Beteg: " + String(patientId));
      if (value < 90.0)
        Serial.println("⚠ Várható riasztás a szerveren!");
      else
        Serial.println("✓ Normál tartomány (≥ 90%)");

    } else if (String(type) == "temperature") {
      value = (float)map(raw, 0, 4095, 360, 380) / 10.0f;
      Serial.println("\n[Ciklus " + String(cycleIndex) + "] Testhőmérséklet – Beteg: " + String(patientId));
      if (value < 36.0 || value > 38.0)
        Serial.println("⚠ Várható riasztás a szerveren!");
      else
        Serial.println("✓ Normál tartomány (36,0–38,0 °C)");

    } else {
      value = 0.0;
      Serial.println("\n[Ciklus " + String(cycleIndex) + "] Ismeretlen típus: " + String(type));
    }

    sendMeasurement(patientId, type, value);

    // Következő ciklus
    cycleIndex = (cycleIndex + 1) % (PATIENT_COUNT * TYPE_COUNT);
  }
}
