============================================================
  ChronoCare – IoT-based Healthcare Monitoring System
  László Páll – Bachelor's Thesis, University of Nyíregyháza, 2025
============================================================

DEVELOPMENT ENVIRONMENT
------------------------
  Java version  : Java 17 LTS
  Framework     : Spring Boot 3.4.5
  Build tool    : Apache Maven 3.x
  IDE           : IntelliJ IDEA (Ultimate recommended)
  Database      : H2 (file-based, embedded)
  MQTT broker   : HiveMQ public broker (ssl://broker.hivemq.com:8883)
  IoT simulator : Wokwi (ESP32 online simulator)

SYSTEM REQUIREMENTS
-------------------
  - Java 17 or higher (https://adoptium.net/)
  - Apache Maven 3.8 or higher
  - Internet connection (required for MQTT broker)

OPENING THE PROJECT
-------------------
  1. Download or extract the project.
  2. Open in IntelliJ IDEA:
       File → Open → select the project root folder (containing pom.xml)
  3. Maven will automatically download all dependencies.

RUNNING LOCALLY
---------------
  From the command line (using Maven):

    cd chronocare
    mvn spring-boot:run

  Or from IntelliJ IDEA:
    Run the ChronoCareApplication.java main class.

  The application will start at:
    http://localhost:8080

DEFAULT LOGIN CREDENTIALS
--------------------------
  The database is automatically populated with sample data on first startup.

  Doctor accounts:
    Username: house    Password: admin   (Dr. House)
    Username: strange  Password: admin   (Dr. Strange)

  Patient accounts:
    Username: janos    Password: 1234    (Teszt János, age 65)
    Username: maria    Password: 1234    (Minta Mária, age 42)

  New patients can also be registered via the main page.

H2 DATABASE CONSOLE (development)
-----------------------------------
  URL  : http://localhost:8080/h2-console
  JDBC : jdbc:h2:file:./data/chronodb
  User : sa
  Pass : (leave empty)

MQTT CONNECTION
---------------
  The system uses a TLS-encrypted MQTT connection:
    Broker : ssl://broker.hivemq.com:8883
    Topic  : patient/{patientId}/measurements
    QoS    : 1

  The IoT simulator (Wokwi ESP32) sketch can be found in
  the /wokwi folder of the GitHub repository.
  Wokwi simulator: https://wokwi.com

DATABASE INITIALIZATION (localhost setup)
------------------------------------------
  The H2 file-based database is created automatically on first startup
  at ./data/chronodb. No separate database server installation is required.

  To start with a fresh database, delete the ./data/ folder
  and restart the application.

PROJECT STRUCTURE
-----------------
  src/main/java/com/chronocare/chronocare/
    ├── controller/
    │     ├── DashboardController.java
    │     └── HomeController.java
    ├── model/
    │     ├── Doctor.java
    │     ├── Patient.java
    │     └── HealthMeasurement.java
    ├── repository/
    │     ├── DoctorRepository.java
    │     ├── PatientRepository.java
    │     └── MeasurementRepository.java
    ├── service/
    │     ├── MonitoringService.java
    │     ├── MQTTconfig.java
    │     ├── MQTTListener.java
    │     └── SecurityConfig.java
    └── ChronoCareApplication.java

  src/main/resources/
    ├── templates/   (Thymeleaf HTML templates)
    └── application.properties

GITHUB REPOSITORY
-----------------
  https://github.com/vertussgn/ChronoCare

============================================================
