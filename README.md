ChronoCare - IoT Alapú Egészségügyi Monitoring Rendszer
![alt text](https://img.shields.io/badge/Java-17%2B-orange)
![alt text](https://img.shields.io/badge/Spring_Boot-3.x-green)
![alt text](https://img.shields.io/badge/Frontend-Thymeleaf-blue)
![alt text](https://img.shields.io/badge/License-MIT-lightgrey)
📌 Projekt Leírása
A ChronoCare egy prototípus szoftver, amely krónikus betegek távfelügyeletét támogatja IoT (Internet of Things) technológiák szimulálásával. A rendszer célja, hogy a páciensek által mért élettani adatokat (pl. vérnyomás, vércukorszint) valós időben gyűjtse, tárolja és vizualizálja az orvosok számára.
A projekt szakdolgozat keretében készült, bemutatva egy modern IoMT (Internet of Medical Things) architektúra alapjait, beleértve az automatikus riasztási logikát kritikus értékek esetén.
🚀 Funkciók
Adatgyűjtés és Tárolás: Páciensek élettani adatainak (vérnyomás, vércukor) fogadása és perzisztens tárolása.
Automatikus Riasztási Rendszer: A rendszer figyeli a beérkező adatokat, és vizuálisan riaszt (piros jelzés), ha egy érték átlépi az orvosilag meghatározott küszöböt (pl. vérnyomás > 140 Hgmm).
Orvosi Műszerfal (Dashboard): Áttekinthető webes felület, ahol az orvosok nyomon követhetik a páciensek állapotát és kórtörténetét.
IoT Szimuláció: Beépített tesztfelület, amellyel szenzoradatok generálhatók és küldhetők be a rendszerbe manuálisan (hardveres eszköz hiányában).
🛠 Technológiai Háttér
A projekt a következő technológiákra épül:
Backend: Java 17, Spring Boot 3.x (Web, Data JPA)
Adatbázis: H2 Database (fejlesztéshez/memória alapú) / MySQL (élesítéshez)
Frontend: Thymeleaf (Szerver oldali renderelés), Bootstrap 5 (CSS keretrendszer)
Build Tool: Maven
IDE: IntelliJ IDEA
📂 Projekt Struktúra (MVC)
code
Code
src/main/java/com/chronocare
├── controller   # Webes kérések kezelése (DashboardController)
├── model        # Adatbázis entitások (Patient, HealthMeasurement)
├── repository   # Adatbázis műveletek (JPA interfészek)
├── service      # Üzleti logika és riasztás (MonitoringService)
└── ChronoCareApplication.java
