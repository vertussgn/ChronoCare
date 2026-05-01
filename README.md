============================================= CHRONOCARE – OLVASÁSI ÚTMUTATÓ

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

  Fontos: az ID-kat az alkalmazás indítása után a H2 konzolban vagy a pácienslista
  oldalon tudod leellenőrizni (az adatbázis minden indításkor új ID-kat generálhat).

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
    TLS:            espClient.setInsecure() – tanúsítvány ellenőrzés nélküli TLS

  Megjegyzés: az espClient.setInsecure() Wokwi szimulációhoz elegendő.
  Valódi éles ESP32 eszköznél CA tanúsítvány ellenőrzés ajánlott.


MEGJEGYZÉSEK

Az adatbázis H2 in-memory módban fut; az alkalmazás leállításakor az adatok törlődnek.
A H2 konzol elérhető fejlesztői módban: http://localhost:8080/h2-console
  JDBC URL: jdbc:h2:mem:chronocaredb  |  Felhasználó: sa  |  Jelszó: password
Az MQTT kapcsolat automatikusan újracsatlakozik hálózati kiesés esetén.
Éles környezetben ajánlott az H2-t PostgreSQL vagy MySQL adatbázisra cserélni,
és az MQTT brokert saját, hitelesített szerverre váltani.


=============================================
CHRONOCARE – USER GUIDE
=============================================
APPLICATION DESCRIPTION

ChronoCare is an IoT-based healthcare monitoring web application designed to record, display, and manage real-time patient data and alerts. The application receives measurement data via the MQTT protocol from ESP32/Wokwi IoT devices and makes it accessible through a web interface for doctors and patients.

Key Features
Medical Dashboard: View list of patients and individual profiles
Patient Portal: Access personal measurements and alerts
MQTT Integration: Real-time IoT data reception with TLS encryption (HiveMQ broker)
IoT Simulator: Manual measurement submission via the medical interface
Automatic Alerts: Threshold evaluation based on age
Biological Validation: Filters invalid measurement data before saving
Secure Authentication: BCrypt password hashing, role-based access (DOCTOR / PATIENT)
Built-in Database: H2 in-memory database (development mode)
INSTALLATION GUIDE

If the required tools are not yet installed:

Install Java Development Kit (JDK) 17+
Visit: https://www.oracle.com/java/technologies/downloads/
Download the Windows x64 Installer version (JDK 17+)
Run the installer and follow the instructions

Verify installation:

java -version
Install Apache Maven
Visit: https://maven.apache.org/download.cgi
Download the Binary zip archive
Extract it (e.g., to C:\Program Files\Maven)

Add to PATH:

C:\Program Files\Maven\bin

Verify installation:

mvn -version
Install IntelliJ IDEA
Visit: https://www.jetbrains.com/idea/download/
Download the Community (free) version
Run the installer and follow the instructions
SYSTEM REQUIREMENTS
Operating System: Windows 10 / Windows 11 / Linux / macOS (64-bit)
Java Development Kit (JDK) 17 or newer
Apache Maven 3.8 or newer
Internet connection (MQTT broker: broker.hivemq.com:8883)
Minimum RAM: 512 MB
Recommended RAM: 1 GB or more
Free disk space: at least 200 MB
IMPORTING THE PROJECT INTO INTELLIJ IDEA
Extract the zip file to a folder
Open IntelliJ IDEA
Select File > Open
Navigate to the extracted ChronoCare-main folder
Click OK
IntelliJ will automatically detect the Maven project
Wait for dependencies to download (may take a few minutes)

If Maven sync does not start automatically:

Right-click pom.xml → Maven → Reload Project
RUNNING THE APPLICATION FROM THE DEVELOPMENT ENVIRONMENT

Locate the main class:

src/main/java/com/chronocare/chronocare/ChronoCareApplication.java
Right-click → Run 'ChronoCareApplication.main()'
OR click the green run icon

After startup, the application is available at:

http://localhost:8080
BUILT-IN TEST ACCOUNTS

Automatically created on first run:

Doctor 1: username: house | password: admin
Doctor 2: username: strange | password: admin
Patient 1: username: janos | password: 1234
Patient 2: username: maria | password: 1234
BUILDING THE JAR FILE

Open terminal in the project root:

mvn clean package

Generated file:

target/chronocare-0.0.1-SNAPSHOT.jar

Run the JAR:

java -jar target/chronocare-0.0.1-SNAPSHOT.jar
MQTT CONFIGURATION
Broker URL: ssl://broker.hivemq.com:8883
Protocol: TLSv1.3
Topic: patient/{patientId}/measurements
QoS: 1 (at least once delivery)
Keep-alive: 60 seconds
Connection timeout: 10 seconds
Example JSON Payloads
{"type": "blood_pressure", "value": 125.0}
{"type": "heart_rate", "value": 72.0}
{"type": "blood_sugar", "value": 5.2}
{"type": "spo2", "value": 97.0}
{"type": "temperature", "value": 36.8}
NORMAL RANGES AND ALERT THRESHOLDS
Blood Pressure (systolic)
< 18 years: 90–120 mmHg
18–64 years: 100–140 mmHg
≥ 65 years: 100–150 mmHg
Other Measurements
Heart rate: 60–100 BPM
Blood sugar: 3.9–7.8 mmol/L
Oxygen saturation: ≥ 90% (SpO₂)
Body temperature: 36.0–38.0 °C

Values outside these ranges trigger alerts (highlighted in red).

TECHNOLOGIES USED
Java 17+
Spring Boot 3
Spring Security (BCrypt, role-based access)
Spring Integration MQTT (Eclipse Paho, TLS)
Thymeleaf
Bootstrap 5.1.3
H2 Database
Lombok
Apache Maven
JSON.org
ArduinoJson
Eclipse Paho / PubSubClient
Wokwi
WOKWI IOT SIMULATOR SETUP
Required Libraries (libraries.txt)
WiFiClientSecure
PubSubClient
ArduinoJson
Hardware Configuration
Microcontroller: ESP32 DevKit
Input: 1 potentiometer (GPIO34 / ADC)
Connection: TLS-secured WiFi (Wokwi-GUEST)
RUNNING THE SIMULATOR
Visit: https://wokwi.com
Create a new ESP32 project
Paste the sketch.ino content
Add required libraries
Add a potentiometer (GPIO34)
Click ▶ Start Simulation
Monitor MQTT messages in Serial Monitor
PATIENT ID CONFIGURATION
const int PATIENT_IDS[]  = {8, 9};
const int PATIENT_AGES[] = {65, 42};
const int PATIENT_COUNT  = 2;

Note: IDs may change on each startup. Check via H2 console or patient list.

MEASUREMENT CYCLE

The simulator sends data every 3 seconds:

Blood pressure – Patient A
Blood pressure – Patient B
Heart rate – Patient A
Heart rate – Patient B
... (5 types × 2 patients = 10-cycle loop)
Value ranges (based on potentiometer)
Blood pressure: 100–140 mmHg
Heart rate: 60–100 BPM
Blood sugar: 3.9–7.8 mmol/L
SpO₂: 90–100%
Temperature: 36.0–38.0 °C
MQTT TOPIC STRUCTURE
Direction: ESP32 → HiveMQ → Spring Boot server
Topic: patient/{patientId}/measurements
Payload: JSON
QoS: 0 (ESP32 side)
TLS: espClient.setInsecure() (no certificate validation)

Note: For production, certificate validation is recommended.

NOTES
H2 database runs in-memory (data is lost on shutdown)
H2 console: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:chronocaredb
User: sa
Password: (empty)
MQTT reconnects automatically after connection loss
For production:
Replace H2 with PostgreSQL or MySQL
Use a private, authenticated MQTT broker

If you want, I can also turn this into a polished PDF, README.md, or documentation site.
