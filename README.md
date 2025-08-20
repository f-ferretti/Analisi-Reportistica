# Report Management — Documentazione di Progetto

Microservizio Java/Spring Boot che espone API REST per la generazione di report **studenti**, **corsi** e **docenti** in **JSON** e **PDF**.

---

## Tecnologie

- Java 17
- Spring Boot 3.2.x (Web, Validation, Security, Data JPA)
- PostgreSQL
- iTextPDF 5.5.x (generazione PDF)
- springdoc‑openapi (Swagger UI)
- JJWT (validazione JWT)

---

## Configurazione

### `application.yml`

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
  base-url: http://localhost:8000/api/v1/reports   # endpoint dello stub esterno
server:
  port: 8080
```

### Chiave pubblica JWT

- Percorso: `src/main/resources/jwt/public_key.pem`
- Caricata e usata da `TokenJWTService` per la verifica della firma (RS256).

---

## Sicurezza

- Tutte le API (eccetto `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/health`, `/health`) richiedono **JWT** nell’header:

  `Authorization: Bearer <token>`

- Il filtro `JwtAuthenticationFilter`:

  - valida la firma tramite `TokenJWTService` (chiave pubblica RSA),
  - estrae `sub` (utente) e l’array `roles` dal token,
  - mappa i ruoli in `ROLE_*` e popola il `SecurityContext`.

- I controller usano `@PreAuthorize` dove serve; in caso di mancanza/invalidità del token rispondono con **401** JSON uniforme.

---

## Struttura dei pacchetti

```
src/main/java/it/unimol/report_management/
├─ controller/              # REST controller e mapping degli endpoint
├─ service/                 # logica applicativa
├─ client/                  # StubClient per chiamate al servizio esterno
├─ dto/                     # DTO di input/output
├─ util/                    # PdfUtil (iText), util varie
├─ security/                # SecurityConfig, JwtAuthenticationFilter
├─ config/                  # OpenAPIConfig, RestTemplate/WebClient
└─ web/                     # ApiError
```

---

## API

Base path per gruppo:

- Studenti: `/api/v1/students`
- Corsi: `/api/v1/courses`
- Docenti: `/api/v1/teachers`

### Studenti

#### 1) Esami superati

`GET /api/v1/students/{matricola}/exams/passed`

- `matricola`: 6 cifre (regex `^\d{6}$`).
- Risposta: `List<ExamResultDTO>`.

`ExamResultDTO` (campi principali):

```json
{
  "codiceCorso": "ST101",
  "nomeCorso": "Programmazione 1",
  "cfu": 9,
  "voto": 28,
  "lode": false,
  "data": "2025-02-20",
  "aaSuperamento": 2025,
  "aaFrequenza": 2024
}
```

#### 2) Esami mancanti (piano – superati)

`GET /api/v1/students/{matricola}/exams/pending`

- `matricola`: 6 cifre.
- Risposta: `List<PlanItemDTO>` con campi: `codice`, `nome`, `cfu`, `annoCorso`, `obbligatorio`.

#### 3) Progresso CFU

`GET /api/v1/students/{matricola}/credits/progress`

Risposta `CreditsProgressDTO`:

```json
{
  "earnedCfu": 120,
  "totalCfu": 180,
  "missingCfu": 60,
  "percent": 66.67
}
```

#### 4) Stima voto di laurea

`GET /api/v1/students/{matricola}/graduation/estimate`

- Query param opzionali: `annoAccademico` (alias accettati: `aa`, `anno`). Se assente, usa anno corrente.
- Risposta `GraduationEstimateDTO`:

```json
{
  "weightedAvg30": 27.4,
  "base110": 100,
  "finalEstimate": 107
}
```

#### 5) Report completo (PDF)

`GET /api/v1/students/{matricola}/summary.pdf`

- Query param opzionale: `annoAccademico`.
- Risposta: PDF (header `Content-Disposition: attachment; filename=student_summary_{matricola}[_AA].pdf`).

### Corsi

#### 1) Iscritti per anno

`GET /api/v1/courses/{courseCode}/enrollments`

- `courseCode`: `^[A-Za-z0-9_-]{2,32}$`.
- Risposta: `List<Map<String,Integer>>` normalizzata a coppie `{ "anno": <int>, "iscritti": <int> }`.

#### 2) Voti del corso (JSON)

`GET /api/v1/courses/{courseCode}/grades`

- Query param opzionale: `annoAccademico` (se assente il controller risolve anno corrente).
- Risposta: `List<ExamResultDTO>` (come sopra).

#### 3) Distribuzione voti corso (PDF)

`GET /api/v1/courses/{courseCode}/grades/distribution.pdf`

- Query param obbligatorio: `annoAccademico`.
- Risposta: PDF con istogramma dei voti.

### Docenti

#### 1) Distribuzione voti docente (PDF)

`GET /api/v1/teachers/{docenteId}/grades/distribution.pdf`

- `docenteId`: `^DOC\d{3}$` (es. `DOC123`).
- Query param obbligatorio: `annoAccademico`.
- Risposta: PDF con istogramma aggregato sui corsi del docente.

#### 2) Consistenza anno‑su‑anno (JSON)

`GET /api/v1/teachers/{docenteId}/consistency`

- Query param: `courseCode` (obbligatorio), `from` e `to` (opzionali; default: [anno corrente − 4 .. anno corrente]).
- Risposta: `TeacherConsistencyDTO` con:
  - `avgByYear` (media voti per anno),
  - `passRateByYear` (tasso di superamento),
  - `stddev`, `trendSlope`, oltre a `teacherId`, `courseCode`, `from`, `to`, `yearsCount`.

---

## Error handling

Formato uniforme (classe `ApiError`):

```json
{
  "timestamp": "2025-08-20T12:30:00+02:00",
  "status": 404,
  "error": "Not Found",
  "message": "Risorsa non trovata",
  "path": "/api/v1/...",
  "details": {"cause": "..."},
  "correlationId": null
}
```

Codici usati: 400, 401, 403, 404, 422, 500.

---

## Generazione PDF

- Implementata in `util/PdfUtil` tramite iText 5.
- Supporta: titolo, metadati, conversione di `BufferedImage` in PDF (`imageToPdf`), e report riassuntivo studente (`studentSummary`).
- I controller impostano header HTTP per il download (`Content-Disposition: attachment`).

---

## Caching dei report&#x20;

- Cache persistente su PostgreSQL, tabella `report_cache` con colonne: `id`, `report_type`, `target_id`, `parameters`, `format`, `report_data` (`bytea`), `generated_at`.
- TTL default: 24h.
- Key di cache: `(report_type, target_id, parameters, format)`.
- Applicata ai PDF: student summary, distribuzione voti corso, distribuzione voti docente.

---

## Build & Run

```bash
mvn clean package
java -jar target/*.jar
# oppure
mvn spring-boot:run
```

Swagger UI: `http://localhost:8080/swagger-ui/index.html`
Health: `GET /actuator/health`