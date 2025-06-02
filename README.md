# Microservizio Analisi e Reportistica

## Descrizione del Progetto

Il microservizio **Analisi e Reportistica** è responsabile della generazione di report di base sull'attività della piattaforma universitaria. Fornisce informazioni aggregate e analisi dei dati accademici per supportare decisioni gestionali e monitorare le performance di studenti, corsi e docenti.

## API Endpoints

### Studenti

#### `GET /api/v1/reports/students/{studentId}/activity`
Restituisce attività di esami e compiti svolti.

**Query Params:**
- `startDate`: Data di inizio (formato `YYYY-MM-DD`, opzionale)
- `endDate`: Data di fine (formato `YYYY-MM-DD`, opzionale)
- `format`: Formato di output (`json`, `csv`, `pdf`, default: `json`)

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
- `startDate`: Data di inizio (formato `YYYY-MM-DD`, opzionale)
- `endDate`: Data di fine (formato `YYYY-MM-DD`, opzionale)
- `format`: Formato di output (`json`, `csv`, `pdf`, default: `json`)

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
- `startDate`: Data di inizio (formato `YYYY-MM-DD`, opzionale)
- `endDate`: Data di fine (formato `YYYY-MM-DD`, opzionale)
- `format`: Formato di output (`json`, `csv`, `pdf`, default: `json`)

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
- `startDate`: Data di inizio (formato `YYYY-MM-DD`, opzionale)
- `endDate`: Data di fine (formato `YYYY-MM-DD`, opzionale)
- `format`: Formato di output (`json`, `csv`, `pdf`, default: `json`)

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
| 401    | Token non presente o invalido  |
| 404    | Risorsa non trovata            |
| 500    | Errore interno                 |

## 🔐 Sicurezza e Autenticazione

Tutti gli endpoint REST richiedono l’autenticazione tramite **JWT**. Il token deve essere incluso nell’header HTTP `Authorization` nel formato:

```
Authorization: Bearer <token>
```

Il microservizio verifica la validità e l'integrità del token controllando la **firma digitale tramite chiave pubblica RSA**, fornita dal microservizio di autenticazione esterno.

## DTO (Data Transfer Objects)

### StudentReportDto

```java
public class StudentReportDto {
    private String studentId;
    private String studentName;
    private String studentEmail;
    private Integer enrolledCourses;
    private Integer completedExams;
    private Double averageGrade;
    private Double attendanceRate;
    private Integer assignmentsSubmitted;
    private Integer assignmentsTotal;
    private LocalDateTime reportGeneratedAt;
}
```

### CourseReportDto

```java
public class CourseReportDto {
    private String courseId;
    private String courseName;
    private String courseCode;
    private Integer enrolledStudents;
    private Double averageGrade;
    private Double passRate;
    private Double attendanceRate;
    private Double assignmentCompletionRate;
    private LocalDateTime reportGeneratedAt;
}
```

### TeacherReportDto

```java
public class TeacherReportDto {
    private String teacherId;
    private String teacherName;
    private String teacherEmail;
    private Integer coursesTeaching;
    private Integer totalStudents;
    private Double averageFeedback;
    private Double responseRate;
    private LocalDateTime reportGeneratedAt;
}
```

### ReportRequestDto

```java
public class ReportRequestDto {
    private String reportType;
    private String targetId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String format;
}
```

## Architettura del Sistema

### Tecnologie Utilizzate

- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL**
- **RabbitMQ**
- **REST API**
- **Maven**

### Pattern Architetturali

- Microservizi Architecture
- Repository Pattern
- DTO Pattern
- Service Layer

## Integrazione con Altri Microservizi

- **Gestione Utenti e Ruoli**
- **Gestione Corsi**
- **Gestione Esami**
- **Gestione Compiti**
- **Gestione Presenze**
- **Valutazione e Feedback**
