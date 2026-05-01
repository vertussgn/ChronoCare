=======================================================================================================================================
============================================= CHRONOCARE – OLVASÁSI ÚTMUTATÓ
=======================================================================================================================================
ALKALMAZÁS LEÍRÁSA

A ChronoCare egy IoT alapú egészségügyi monitoring webalkalmazás, amely valós idejű betegadatok rögzítésére, megjelenítésére és riasztások kezelésére szolgál. Az alkalmazás MQTT protokollon keresztül fogadja az ESP32/Wokwi IoT eszközök által küldött mérési adatokat, és ezeket webes felületen teszi elérhetővé orvosok és betegek számára.

Főbb funkciók:

Orvosi műszerfal: páciensek listája és egyéni adatlapok megjelenítése
Páciens portál: saját mérési adatok és riasztások megtekintése
MQTT integráció: valós idejű IoT adatfogadás TLS titkosítással (HiveMQ broker)
IoT Szimulátor: orvosi felületen manuális mérésbeküldési lehetőség
Automatikus riasztás: életkor-alapú küszöbértékek vizsgálata
Biológiai validáció: érvénytelen mérési adatok szűrése mentés előtt
Biztonságos belépés: BCrypt jelszótitkosítás, szerepkör-alapú hozzáférés (DOCTOR / PATIENT)
Beépített adatbázis: H2 in-memory adatbázis (fejlesztői mód)


TELEPÍTÉSI ÚTMUTATÓ

Ha a szükséges eszközök még nincsenek telepítve a gépen:

Java Development Kit (JDK) 17+ telepítése:

  Látogass el a https://www.oracle.com/java/technologies/downloads/ oldalra
  Töltsd le a Windows x64 Installer verziót (JDK 17+)
  Futtasd a telepítőt és kövesd az utasításokat
  Ellenőrizd a telepítést: java -version

Apache Maven telepítése:

  Látogass el a https://maven.apache.org/download.cgi oldalra
  Töltsd le a Binary zip archive fájlt
  Csomagold ki pl. C:\Program Files\Maven mappába
  Add hozzá a PATH környezeti változóhoz: C:\Program Files\Maven\bin
  Ellenőrizd a telepítést: mvn -version

IntelliJ IDEA telepítése:

  Látogass el a https://www.jetbrains.com/idea/download/ oldalra
  Töltsd le a Community (ingyenes) verziót
  Futtasd a telepítőt és kövesd az utasításokat


RENDSZERKÖVETELMÉNYEK

Operációs rendszer: Windows 10 / Windows 11 / Linux / macOS (64 bit)
Java Development Kit (JDK) 17 vagy újabb verzió
Apache Maven 3.8 vagy újabb verzió
Internetkapcsolat (MQTT broker elérése: broker.hivemq.com:8883)
Minimális RAM: 512 MB
Ajánlott RAM: 1 GB vagy több
Szabad lemezterület: minimum 200 MB


A PROJEKT IMPORTÁLÁSA INTELLIJ IDEA-BA

1. Csomagold ki a zip fájlt egy tetszőleges mappába
2. Nyisd meg az IntelliJ IDEA fejlesztői környezetet
3. Válaszd a File > Open menüpontot
4. Navigálj a kicsomagolt "ChronoCare-main" mappába
5. Kattints az OK gombra
6. Az IntelliJ IDEA automatikusan felismeri a Maven projektet
7. Várd meg, amíg a Maven letölti a szükséges függőségeket
   (ez első alkalommal néhány percet vehet igénybe)
8. Ha a Maven szinkronizálás nem indul el automatikusan:
   jobb klikk a pom.xml fájlra > Maven > Reload Project


AZ ALKALMAZÁS FUTTATÁSA FEJLESZTŐI KÖRNYEZETBŐL

A projekt betöltése után keresd meg a főosztályt:
  src/main/java/com/chronocare/chronocare/ChronoCareApplication.java

Jobb klikk a fájlra > Run 'ChronoCareApplication.main()'
VAGY nyisd meg a fájlt és kattints a zöld nyíl ikonra a szerkesztő mellett.

Az alkalmazás elindulás után elérhető: http://localhost:8080


BEÉPÍTETT TESZTFIÓKOK (automatikusan létrehozva)

  Orvos 1:    felhasználónév: house    | jelszó: admin
  Orvos 2:    felhasználónév: strange  | jelszó: admin
  Beteg 1:    felhasználónév: janos    | jelszó: 1234
  Beteg 2:    felhasználónév: maria    | jelszó: 1234

Ezek az adatok az alkalmazás első indításakor automatikusan bekerülnek az adatbázisba.


LEFORDÍTOTT JAR FÁJL LÉTREHOZÁSA

Nyisd meg a terminált a projekt gyökérmappájában
(IntelliJ IDEA-ban: View > Tool Windows > Terminal)

Futtasd le a következő parancsot:

  mvn clean package

A lefordított JAR fájl a target/ mappában jön létre: target/chronocare-0.0.1-SNAPSHOT.jar

A JAR fájl futtatása:

  java -jar target/chronocare-0.0.1-SNAPSHOT.jar


MQTT KONFIGURÁCIÓ

Az alkalmazás a HiveMQ publikus brokert használja TLS titkosított kapcsolaton keresztül.

  Broker URL:  ssl://broker.hivemq.com:8883
  Protokoll:   TLSv1.3
  Topic:       patient/{patientId}/measurements
  QoS szint:   1 (legalább egyszer kézbesítve)
  Keep-alive:  60 másodperc
  Kapcsolódási timeout: 10 másodperc

Az IoT eszköznek (ESP32 / Wokwi) JSON formátumban kell küldenie az adatokat:

  {"type": "blood_pressure", "value": 125.0}
  {"type": "heart_rate",     "value": 72.0}
  {"type": "blood_sugar",    "value": 5.2}
  {"type": "spo2",           "value": 97.0}
  {"type": "temperature",    "value": 36.8}


NORMÁL MÉRÉSI TARTOMÁNYOK ÉS RIASZTÁSI KÜSZÖBÖK

  Vérnyomás (szisztolés):
    < 18 év:   90–120 Hgmm
    18–64 év:  100–140 Hgmm
    ≥ 65 év:   100–150 Hgmm

  Pulzus:      60–100 BPM
  Vércukor:    3,9–7,8 mmol/l
  Véroxigén:   ≥ 90 % (SpO₂)
  Testhő:      36,0–38,0 °C

Ha egy érték kívül esik a tartományon, az alkalmazás RIASZTÁS státuszt rendel hozzá,
és a mérési előzményekben piros kiemeléssel jelzi.


FELHASZNÁLT TECHNOLÓGIÁK

  Java 17+
  Spring Boot 3 (webalkalmazás keretrendszer)
  Spring Security (BCrypt jelszótitkosítás, hozzáférés-vezérlés)
  Spring Integration MQTT (Eclipse Paho kliens, TLS kapcsolat)
  Thymeleaf (szerver oldali HTML sablon motor)
  Bootstrap 5.1.3 (reszponzív felhasználói felület)
  H2 Database (beépített in-memory adatbázis, fejlesztői módhoz)
  Lombok (getter/setter automatikus generálás)
  Apache Maven (projektmenedzsment és build eszköz)
  JSON.org (MQTT payload feldolgozás)
  ArduinoJson (ESP32 JSON sorosítás)
  Eclipse Paho / PubSubClient (ESP32 MQTT kliens)
  Wokwi (ESP32 online szimulációs platform)


WOKWI IoT SZIMULÁTOR BEÁLLÍTÁSA

A ChronoCare rendszerhez tartozik egy ESP32 alapú hardver szimulátor, amely Wokwi online
platformon futtatható, és valódi MQTT üzeneteket küld a szervernek.

  Szükséges könyvtárak (Wokwi libraries.txt):
    WiFiClientSecure
    PubSubClient
    ArduinoJson

  Hardver konfiguráció:
    Mikrokontroller:  ESP32 DevKit
    Bemenet:          1 db potméter (analóg, GPIO34 / ADC pin)
    Kapcsolat:        TLS titkosított WiFi (Wokwi-GUEST hálózat)

  A SZIMULÁTOR FUTTATÁSA WOKWI-N

  1. Látogass el a https://wokwi.com oldalra
  2. Hozz létre új ESP32 projektet
  3. Illeszd be a sketch.ino tartalmát a szerkesztőbe
  4. Az libraries.txt fájlba add meg a szükséges könyvtárakat
  5. Adj hozzá egy potmétert a diagramhoz (GPIO34-re kötve)
  6. Kattints a ▶ Start Simulation gombra
  7. A Serial Monitor ablakban követheted az MQTT küldéseket

  BETEG AZONOSÍTÓK BEÁLLÍTÁSA

  A kódban a PATIENT_IDS[] tömbben kell megadni az adatbázisban lévő beteg ID-kat:

    const int PATIENT_IDS[]  = {8, 9};   
    const int PATIENT_AGES[] = {65, 42}; 
    const int PATIENT_COUNT  = 2;

  MÉRÉSI CIKLUS MŰKÖDÉSE

  A szimulátor 3 másodpercenként küld egy mérést, körbe-körbe váltva a betegek
  és a mérési típusok között:

    Ciklus 1:  blood_pressure  – Beteg A
    Ciklus 2:  blood_pressure  – Beteg B
    Ciklus 3:  heart_rate      – Beteg A
    Ciklus 4:  heart_rate      – Beteg B
    ... (5 típus × 2 beteg = 10 ciklusonként ismétlődik)

  Az értékeket a potméter állása határozza meg:
    Vérnyomás:      100–140 Hgmm  (potméter alapján)
    Pulzus:         60–100 BPM
    Vércukor:       3,9–7,8 mmol/l
    Véroxigén:      90–100 %
    Testhőmérséklet: 36,0–38,0 °C

  A Serial Monitorban a program előre jelzi, hogy várható-e riasztás a szerveren.

  MQTT TOPIC STRUKTÚRA (ESP32 → Szerver)

    Küldés iránya:  ESP32 → broker.hivemq.com:8883 → Spring Boot szerver
    Topic formátum: patient/{patientId}/measurements
    Payload (JSON): {"type": "blood_pressure", "value": 125.0}
    QoS:            0 (ESP32 oldalon)
    TLS:            espClient.setInsecure() 

  Megjegyzés: az espClient.setInsecure() Wokwi szimulációhoz elegendő.


MEGJEGYZÉSEK

Az adatbázis H2 in-memory módban fut; az alkalmazás leállításakor az adatok törlődnek.
A H2 konzol elérhető fejlesztői módban: http://localhost:8080/h2-console
  JDBC URL: jdbc:h2:mem:chronocaredb  |  Felhasználó: sa  |  Jelszó: password
Az MQTT kapcsolat automatikusan újracsatlakozik hálózati kiesés esetén.

CHRONOCARE – USER GUIDE
APPLICATION OVERVIEW

ChronoCare is an IoT-based healthcare monitoring web application designed for recording, displaying, and managing real-time patient data and alerts. The application receives measurement data from ESP32/Wokwi IoT devices via the MQTT protocol and makes it available through a web interface for doctors and patients.

Main Features
Medical Dashboard: display patient lists and individual medical profiles
Patient Portal: view personal measurement history and alerts
MQTT Integration: real-time IoT data reception with TLS encryption (HiveMQ broker)
IoT Simulator: manual measurement submission available from the medical interface
Automatic Alerts: age-based threshold validation for incoming measurements
Biological Validation: invalid measurement values are filtered before saving
Secure Login: BCrypt password encryption and role-based access control (DOCTOR / PATIENT)
Embedded Database: H2 in-memory database (development mode)
INSTALLATION GUIDE

If the required tools are not yet installed on your machine:

Install Java Development Kit (JDK) 17+
Visit: https://www.oracle.com/java/technologies/downloads/
Download the Windows x64 Installer version (JDK 17+)
Run the installer and follow the setup instructions
Verify installation: java -version
Install Apache Maven
Visit: https://maven.apache.org/download.cgi
Download the Binary zip archive
Extract it, for example to: C:\Program Files\Maven
Add the following to the PATH environment variable: C:\Program Files\Maven\bin
Verify installation: mvn -version
Install IntelliJ IDEA
Visit: https://www.jetbrains.com/idea/download/
Download the Community Edition (free)
Run the installer and follow the setup instructions
SYSTEM REQUIREMENTS
Operating System: Windows 10 / Windows 11 / Linux / macOS (64-bit)
Java Development Kit (JDK): version 17 or newer
Apache Maven: version 3.8 or newer
Internet Connection (required for MQTT broker access: broker.hivemq.com:8883)
Minimum RAM: 512 MB
Recommended RAM: 1 GB or more
Free Disk Space: at least 200 MB
IMPORTING THE PROJECT INTO INTELLIJ IDEA
Extract the ZIP archive into any folder
Open IntelliJ IDEA
Select File > Open
Navigate to the extracted ChronoCare-main folder
Click OK
IntelliJ IDEA will automatically recognize the Maven project
Wait for Maven to download the required dependencies
(this may take a few minutes the first time)
If Maven sync does not start automatically:
right-click pom.xml > Maven > Reload Project
RUNNING THE APPLICATION FROM THE DEVELOPMENT ENVIRONMENT

After loading the project, locate the main class:

src/main/java/com/chronocare/chronocare/ChronoCareApplication.java

Right-click the file and select:

Run 'ChronoCareApplication.main()'

OR open the file and click the green run arrow next to the editor.

After startup, the application will be available at:

http://localhost:8080

BUILT-IN TEST ACCOUNTS (CREATED AUTOMATICALLY)
Doctor 1
Username: house
Password: admin
Doctor 2
Username: strange
Password: admin
Patient 1
Username: janos
Password: 1234
Patient 2
Username: maria
Password: 1234

These accounts are automatically inserted into the database on the first startup of the application.

BUILDING THE COMPILED JAR FILE

Open a terminal in the project root directory
(In IntelliJ IDEA: View > Tool Windows > Terminal)

Run the following command:

mvn clean package

The compiled JAR file will be generated in the target/ folder:

target/chronocare-0.0.1-SNAPSHOT.jar

Running the JAR file

java -jar target/chronocare-0.0.1-SNAPSHOT.jar

MQTT CONFIGURATION

The application uses the public HiveMQ broker over a TLS-encrypted connection.

Broker URL: ssl://broker.hivemq.com:8883
Protocol: TLSv1.3
Topic: patient/{patientId}/measurements
QoS Level: 1 (delivered at least once)
Keep-alive: 60 seconds
Connection Timeout: 10 seconds

The IoT device (ESP32 / Wokwi) must send data in JSON format:

{"type": "blood_pressure", "value": 125.0}
{"type": "heart_rate",     "value": 72.0}
{"type": "blood_sugar",    "value": 5.2}
{"type": "spo2",           "value": 97.0}
{"type": "temperature",    "value": 36.8}
NORMAL MEASUREMENT RANGES AND ALERT THRESHOLDS
Blood Pressure (Systolic)
Under 18 years: 90–120 mmHg
18–64 years: 100–140 mmHg
65+ years: 100–150 mmHg
Other Measurements
Heart Rate: 60–100 BPM
Blood Sugar: 3.9–7.8 mmol/L
Blood Oxygen: ≥ 90% (SpO₂)
Body Temperature: 36.0–38.0 °C

If a value falls outside the normal range, the application assigns an ALERT status and highlights the measurement in red in the history view.

TECHNOLOGIES USED
Java 17+
Spring Boot 3 (web application framework)
Spring Security (BCrypt password encryption, access control)
Spring Integration MQTT (Eclipse Paho client, TLS connection)
Thymeleaf (server-side HTML template engine)
Bootstrap 5.1.3 (responsive user interface)
H2 Database (embedded in-memory database for development mode)
Lombok (automatic getter/setter generation)
Apache Maven (project management and build tool)
JSON.org (MQTT payload processing)
ArduinoJson (ESP32 JSON serialization)
Eclipse Paho / PubSubClient (ESP32 MQTT client)
Wokwi (ESP32 online simulation platform)
WOKWI IOT SIMULATOR SETUP

The ChronoCare system includes an ESP32-based hardware simulator that can be run on the Wokwi online platform and sends real MQTT messages to the server.

Required Libraries (libraries.txt in Wokwi)
WiFiClientSecure
PubSubClient
ArduinoJson
Hardware Configuration
Microcontroller: ESP32 DevKit
Input: 1 potentiometer (analog, GPIO34 / ADC pin)
Connection: TLS-encrypted WiFi (Wokwi-GUEST network)
RUNNING THE SIMULATOR IN WOKWI
Visit: https://wokwi.com
Create a new ESP32 project
Paste the contents of sketch.ino into the editor
Add the required libraries to libraries.txt
Add a potentiometer to the diagram (connected to GPIO34)
Click ▶ Start Simulation
MQTT messages can be monitored in the Serial Monitor window
CONFIGURING PATIENT IDS

In the code, the database patient IDs must be defined in the PATIENT_IDS[] array:

const int PATIENT_IDS[]  = {8, 9};
const int PATIENT_AGES[] = {65, 42};
const int PATIENT_COUNT  = 2;

Important: after starting the application, these IDs should be verified in the H2 console or on the patient list page, since the database may generate new IDs on each startup.

MEASUREMENT CYCLE LOGIC

The simulator sends one measurement every 3 seconds, alternating between patients and measurement types in sequence:

Cycle 1: blood_pressure – Patient A
Cycle 2: blood_pressure – Patient B
Cycle 3: heart_rate – Patient A
Cycle 4: heart_rate – Patient B
... (5 types × 2 patients = repeats every 10 cycles)

Measurement values are determined by the potentiometer position:

Blood Pressure: 100–140 mmHg
Heart Rate: 60–100 BPM
Blood Sugar: 3.9–7.8 mmol/L
Blood Oxygen: 90–100%
Body Temperature: 36.0–38.0 °C

The Serial Monitor indicates in advance whether the server is expected to generate an alert.

MQTT TOPIC STRUCTURE (ESP32 → SERVER)
Transmission Path: ESP32 → broker.hivemq.com:8883 → Spring Boot server
Topic Format: patient/{patientId}/measurements
Payload (JSON): {"type": "blood_pressure", "value": 125.0}
QoS: 0 (ESP32 side)
TLS: espClient.setInsecure() – TLS without certificate verification

Note: espClient.setInsecure() is sufficient for Wokwi simulation.
For real ESP32 hardware in production, CA certificate validation is strongly recommended.

NOTES
The database runs in H2 in-memory mode; all data is lost when the application stops.
The H2 console is available in development mode at: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:chronocaredb
Username: sa
Password: password
The MQTT connection automatically reconnects after network interruptions.
In production, it is recommended to replace H2 with PostgreSQL or MySQL

