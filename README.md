# Microservizio Report Management

Questo progetto è un microservizio Java basato su Spring Boot che espone API REST per la generazione di report **docenti**, **studenti** e **corsi** in formato JSON o PDF. Supporta autenticazione JWT, persistenza su PostgreSQL e generazione PDF con iText.

---

## Tecnologie e Dipendenze

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data JPA** per l’accesso a PostgreSQL
- **PostgreSQL JDBC Driver** (runtime)
- **Spring Security** + **JWT** per protezione endpoint
- **iTextPDF 5.5.13.3** per generazione PDF
- **springdoc-openapi 2.3.0** per Swagger UI
- **Lombok 1.18.30**

---

## Architettura dei Pacchetti

```
src/main/java/it/unimol/report_management/
├─ controller/         # REST controller con annotazioni OpenAPI in italiano
├─ service/            # Interfaccia ReportService
├─ service/impl/       # Implementazione con forwardRequest e creazione PDF
├─ security/           # JwtAuthenticationFilter
├─ config/             # SecurityConfig (SecurityFilterChain, CORS, JWT bean)
├─ pdf/                # PdfGenerator (logo, header, footer, tabelle)
└─ ReportManagementApplication.java  # classe main Spring Boot
```

---

## Configurazione

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://<HOST>:<PORT>/<DB_NAME>
    driver-class-name: org.postgresql.Driver
    username: <DB_USER>
    password: <DB_PASS>
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
stub:
  base-url: http://localhost:8000/api/v1/reports  # URL del Python stub
server:
  port: 8080
```

- Sostituire `<HOST>`, `<PORT>`, `<DB_NAME>`, `<DB_USER>` e `<DB_PASS>` con i propri valori.
- `stub.base-url` punta al microservizio Python stub.

### Chiave pubblica JWT

- Posizionata in `src/main/resources/jwt/public_key.pem`
- Usata da `TokenJWTService` per validare la firma.

---


## API Endpoints

### Studenti

#### `GET /api/v1/reports/students/{studentId}/activity`
Restituisce attività di esami e compiti svolti.

**Query Params:**
- `startDate`: Data di inizio (formato `YYYY-MM-DD`)
- `endDate`: Data di fine (formato `YYYY-MM-DD`)
- `format`: Formato di output (`json`, `pdf`, default: `json`)

**Esempio Response**
```json
{
  "studentId": "123",
  "assignmentsCompleted": 10,
  "examsTaken": 5
}
```

---

#### `GET /api/v1/reports/students/{studentId}/grades`
**Path Param:** `studentId` – ID dello studente (proveniente dal microservizio Gestione Utenti)  
Restituisce voti degli esami.

**Esempio Response**
```json
{
  "studentId": "123",
  "grades": [28, 30, 27]
}
```

---

#### `GET /api/v1/reports/students/{studentId}/progress`
**Path Param:** `studentId` – ID dello studente (proveniente dal microservizio Gestione Utenti)  
Restituisce la percentuale di completamento del percorso.

**Esempio Response**
```json
{
  "studentId": "123",
  "progressPercentage": 82.5
}
```

---

#### `GET /api/v1/reports/students/{studentId}/average`
**Path Param:** `studentId` – ID dello studente (proveniente dal microservizio Gestione Utenti)  
Restituisce media aritmetica dei voti.

**Esempio Response**
```json
{
  "studentId": "123",
  "averageGrade": 27.3
}
```

---

#### `GET /api/v1/reports/students/{studentId}/completion-rate`
**Path Param:** `studentId` – ID dello studente (proveniente dal microservizio Gestione Utenti)  
Percentuale di completamento degli esami o moduli.

**Esempio Response**
```json
{
  "studentId": "123",
  "completionRate": 0.89
}
```

---

#### `GET /api/v1/reports/students/{studentId}/performance-over-time`
Andamento delle performance mensili.

**Query Params:**
- `startDate`: Data di inizio (formato `YYYY-MM-DD`)
- `endDate`: Data di fine (formato `YYYY-MM-DD`)
- `format`: Formato di output (`json`, `pdf`, default: `json`)

**Esempio Response**
```json
{
  "studentId": "123",
  "monthlyPerformance": {
    "2024-09": 26,
    "2024-10": 28
  }
}
```

---

### Corsi

#### `GET /api/v1/reports/courses/{courseId}/average`
**Path Param:** `courseId` – ID del corso (proveniente dal microservizio Gestione Corsi)  
Media voti nel corso.

**Esempio Response**
```json
{
  "courseId": "INF001",
  "averageGrade": 26.7
}
```

---

#### `GET /api/v1/reports/courses/{courseId}/distribution`
**Path Param:** `courseId` – ID del corso (proveniente dal microservizio Gestione Corsi)  
Distribuzione voti.

**Esempio Response**
```json
{
  "courseId": "INF001",
  "gradeDistribution": {
    "18-20": 5,
    "21-24": 10,
    "25-27": 12,
    "28-30": 8
  }
}
```

---

#### `GET /api/v1/reports/courses/{courseId}/completion-rate`
**Path Param:** `courseId` – ID del corso (proveniente dal microservizio Gestione Corsi)  
Percentuale di completamento del corso.

**Esempio Response**
```json
{
  "courseId": "INF001",
  "completionRate": 0.84
}
```

---

#### `GET /api/v1/reports/courses/{courseId}/performance-over-time`
Andamento delle performance nel tempo.

**Query Params:**
- `startDate`: Data di inizio (formato `YYYY-MM-DD`)
- `endDate`: Data di fine (formato `YYYY-MM-DD`)
- `format`: Formato di output (`json`, `pdf`, default: `json`)

**Esempio Response**
```json
{
  "courseId": "INF001",
  "monthlyPerformance": {
    "2024-09": 25.1,
    "2024-10": 26.8
  }
}
```

---

### Docenti

#### `GET /api/v1/reports/teachers/{teacherId}/ratings`
**Path Param:** `teacherId` – ID del docente (proveniente dal microservizio Gestione Utenti)  
Restituisce valutazioni numeriche ricevute.

**Esempio Response**
```json
{
  "teacherId": "T9876",
  "ratings": [4.2, 4.5, 4.8]
}
```

---

#### `GET /api/v1/reports/teachers/{teacherId}/average`
**Path Param:** `teacherId` – ID del docente (proveniente dal microservizio Gestione Utenti)
Media delle valutazioni.

**Esempio Response**
```json
{
  "teacherId": "T9876",
  "averageRating": 4.5
}
```

---

#### `GET /api/v1/reports/teachers/{teacherId}/feedback`
**Path Param:** `teacherId` – ID del docente (proveniente dal microservizio Gestione Utenti)  
Feedback testuali ricevuti.

**Esempio Response**
```json
{
  "teacherId": "T9876",
  "feedbackList": [
    "Ottima preparazione",
    "Spiegazioni chiare"
  ]
}
```

---

#### `GET /api/v1/reports/teachers/{teacherId}/performance-over-time`
Andamento delle performance docenti nel tempo.

**Query Params:**
- `startDate`: Data di inizio (formato `YYYY-MM-DD`)
- `endDate`: Data di fine (formato `YYYY-MM-DD`)
- `format`: Formato di output (`json`, `pdf`, default: `json`)

**Esempio Response**
```json
{
  "teacherId": "T9876",
  "monthlyPerformance": {
    "2024-09": 4.3,
    "2024-10": 4.6
  }
}
```

---

### Riepilogo Globale

#### `GET /api/v1/reports/summary`
**Nessun parametro richiesto.**
Panoramica generale dell'intera piattaforma.

**Esempio Response**
```json
{
  "totalStudents": 300,
  "averageGradeOverall": 26.1,
  "topCourses": ["INF001", "RETI2024"]
}
```

---

## Gestione Errori

| Codice | Descrizione                    |
|--------|--------------------------------|
| 400    | Parametri errati o assenti     |
| 403    | Token non presente o invalido  |
| 404    | Risorsa non trovata            |
| 500    | Errore interno                 |

## 🔐 Sicurezza e Autenticazione

Tutti gli endpoint REST richiedono l’autenticazione tramite **JWT**. Il token deve essere incluso nell’header HTTP `Authorization` nel formato:

```
Authorization: Bearer <token>
```

Il microservizio verifica la validità e l'integrità del token controllando la **firma digitale tramite chiave pubblica RSA**, fornita dal microservizio di autenticazione esterno.


## Caching dei Report

Il microservizio implementa una cache persistente dei report generati, tramite la classe `ReportCacheReèpsitory` e il repository associato.  
Quando viene richiesta la generazione di un report, viene prima verificata la presenza di una versione già disponibile in cache.  
Se esistente, il report viene restituito immediatamente, altrimenti viene generato, memorizzato e poi restituito.  
Questo migliora le performance e riduce il carico sui dati di origine.

---

## Integrazione con Altri Microservizi

- **Gestione Utenti e Ruoli**
- **Gestione Corsi**
- **Gestione Esami**
- **Gestione Compiti**
- **Gestione Presenze**
- **Valutazione e Feedback**


---

## Generazione PDF

- Usa **iTextPDF 5.5.13.3** in `PdfGenerator`.
- Aggiunge:
    - Logo (`src/main/resources/logo.png`)
    - Titolo (es. "Docente 42: Valutazioni")
    - Tabella con intestazioni
    - Footer con numero pagina
- Titolo generato in `ReportServiceImpl.extractItalianTitle(...)` mappando entità e report.

---

## Build & Avvio

```bash
npm install # (non serve)
mvn clean package
java -jar target/*.jar
```

Oppure:

```bash
mvn spring-boot:run
```

---

## Swagger UI

Visibile su:

```
http://localhost:8080/swagger-ui/index.html
```

Documentazione interattiva di tutti gli endpoint con descrizioni e possibilità di test.

---

## Note

- I DTO vengono creati ad-hoc nel service; non esistono classi DTO fisse per ogni risposta.
- Per disabilitare il database, esgcludere 'DataSourceAutoConfiguration' in `@SpringBootApplication`.