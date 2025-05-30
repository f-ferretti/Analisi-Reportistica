# Microservizio Analisi e Reportistica

## Descrizione del Progetto

Il microservizio **Analisi e Reportistica** è responsabile della generazione di report di base sull'attività della piattaforma universitaria. Fornisce informazioni aggregate e analisi dei dati accademici per supportare decisioni gestionali e monitorare le performance di studenti, corsi e docenti.

## API Endpoints

### Report Studente

#### `GET /api/v1/reports/student/{studentId}/courses`
**Parametri**
- `studentId`: ID univoco dello studente (da Gestione Utenti)

**Output**
```json
[{ "courseId": "INF001", "name": "Basi di Dati" }]
```
**Errori**
- `401 Unauthorized` – Token mancante o invalido

---

#### `GET /api/v1/reports/student/{studentId}/assignments`
**Parametri**
- `studentId`: ID univoco dello studente (da Gestione Utenti)

**Output**
```json
[{ "title": "Compito 1", "status": "consegnato", "grade": 28 }]
```
**Errori**
- `401 Unauthorized`

---

#### `GET /api/v1/reports/student/{studentId}/exams`
**Parametri**
- `studentId`: ID univoco dello studente (da Gestione Utenti)

**Output**
```json
[{ "examId": "EX123", "course": "Basi di Dati", "grade": 30 }]
```
**Errori**
- `401 Unauthorized`

---

#### `GET /api/v1/reports/student/{studentId}/summary`
**Parametri**
- `studentId`: ID univoco dello studente (da Gestione Utenti)

**Output**
```json
{
  "studentId": "123",
  "studentName": "Mario Rossi",
  "studentEmail": "mario.rossi@studenti.unimol.it",
  "enrolledCourses": 6,
  "completedExams": 12,
  "averageGrade": 27.3,
  "attendanceRate": 89.5,
  "assignmentsSubmitted": 10,
  "assignmentsTotal": 12,
  "reportGeneratedAt": "2025-05-30T12:00:00"
}
```
**Errori**
- `401 Unauthorized`

---

#### `POST /api/v1/reports/student`
**Input**
```json
{
  "studentId": "12345",      //(da Gestione Utenti)
  "startDate": "01-09-2024", //(formato  DD-MM-YYYY)    
  "endDate": "31-03-2025",   // (formato  DD-MM-YYYY)
  "format": "pdf"            // Formato del report: "pdf", "json", "csv"
}
```
**Output**
```json
"Exported report for student 12345 in PDF format"
```
**Errori**
- `400 Bad Request` – dati mancanti
- `401 Unauthorized`

---

### Report Corso

#### `GET /api/v1/reports/course/{courseId}/grades`
**Parametri**
- `courseId`: Codice del corso (da Gestione Corsi)

**Output**
```json
[{ "studentId": "123", "grade": 27 }]
```
**Errori**
- `401 Unauthorized`

---

#### `GET /api/v1/reports/course/{courseId}/attendance`
**Parametri**
- `courseId`: Codice del corso (da Gestione Corsi)

**Output**
```json
[{ "studentId": "123", "attendanceRate": 92.5 }]
```
**Errori**
- `401 Unauthorized`

---

#### `GET /api/v1/reports/course/{courseId}/assignments`
**Parametri**
- `courseId`: Codice del corso (da Gestione Corsi)

**Output**
```json
[{ "assignment": "Progetto finale", "completionRate": 87.0 }]
```
**Errori**
- `401 Unauthorized`

---

#### `GET /api/v1/reports/course/{courseId}/summary`
**Parametri**
- `courseId`: Codice del corso (da Gestione Corsi)

**Output**
```json
{
  "courseId": "CS101",
  "courseName": "Programmazione I",
  "courseCode": "INF001",
  "enrolledStudents": 45,
  "averageGrade": 24.8,
  "passRate": 78.5,
  "attendanceRate": 82.3,
  "assignmentCompletionRate": 88.9,
  "reportGeneratedAt": "2025-05-30T12:00:00"
}
```
**Errori**
- `401 Unauthorized`

---

#### `POST /api/v1/reports/course`
**Input**
```json
{
  "courseId": "CS101",    // (da Gestione Corsi)
  "format": "json",       // Formato del report: "pdf", "json", "csv"
  "includeDetails": true  // true per includere dettagli aggiuntivi
}
```
**Output**
```json
"Exported report for course CS101 in JSON format"
```
**Errori**
- `400 Bad Request`
- `401 Unauthorized`

---

### Report Docente

#### `GET /api/v1/reports/teacher/{teacherId}/given-grades`
**Parametri**
- `teacherId`: ID del docente (da Gestione Utenti)

**Output**
```json
[{ "course": "Basi di Dati", "avgGrade": 28.3 }]
```
**Errori**
- `401 Unauthorized`

---

#### `GET /api/v1/reports/teacher/{teacherId}/feedbacks`
**Parametri**
- `teacherId`: ID del docente (da Gestione Utenti)

**Output**
```json
{
  "avgRating": 4.5,
  "comments": ["Molto chiaro", "Disponibile"]
}
```
**Errori**
- `401 Unauthorized`

---

#### `GET /api/v1/reports/teacher/{teacherId}/summary`
**Parametri**
- `teacherId`: ID del docente (da Gestione Utenti)

**Output**
```json
{
  "teacherId": "DOC001",
  "teacherName": "Prof. Giovanni Bianchi",
  "teacherEmail": "g.bianchi@unimol.it",
  "coursesTeaching": 3,
  "totalStudents": 120,
  "averageFeedback": 4.2,
  "responseRate": 85.0,
  "reportGeneratedAt": "2025-05-30T12:00:00"
}
```
**Errori**
- `401 Unauthorized`

---

#### `POST /api/v1/reports/export`
**Input**
```json
{
  "reportType": "teacher",  // "student", "course"
  "targetId": "DOC001",     // ID del soggetto per cui generare il report
  "format": "pdf",          // Formato del report: "pdf", "json", "csv"
  "notify": true            // Se true, invia notifica all’utente
}
```
**Output**
```json
"Exported report of type 'teacher' for DOC001"
```
**Errori**
- `400 Bad Request`
- `401 Unauthorized`

---


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
