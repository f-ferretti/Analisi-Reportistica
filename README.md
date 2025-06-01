# Microservizio Analisi e Reportistica

## Descrizione del Progetto

Il microservizio **Analisi e Reportistica** è responsabile della generazione di report di base sull'attività della piattaforma universitaria. Fornisce informazioni aggregate e analisi dei dati accademici per supportare decisioni gestionali e monitorare le performance di studenti, corsi e docenti.

## API Endpoints

### Report Studente

#### `GET /api/v1/reports/student/{studentId}/courses`
**Parametri**
- `studentId`: ID univoco dello studente (proveniente dal microservizio Gestione Utenti)

**Output**
```json
[
  {
    "courseId": "INF001",
    "name": "Basi di Dati"
  }
]
```

| Campo       | Significato                 | Origine                      |
|-------------|------------------------------|------------------------------|
| `courseId`  | Codice identificativo corso  | Microservizio Corsi          |
| `name`      | Nome del corso               | Microservizio Corsi          |

---

#### `GET /api/v1/reports/student/{studentId}/assignments`
**Parametri**
- `studentId`: ID univoco dello studente (proveniente dal microservizio Gestione Utenti)

**Output**
```json
[
  {
    "title": "Compito 1",
    "status": "consegnato",
    "grade": 28
  }
]
```

| Campo    | Significato           | Origine                    |
|----------|------------------------|----------------------------|
| `title`  | Titolo del compito     | Microservizio Compiti      |
| `status` | Stato di consegna      | Microservizio Compiti      |
| `grade`  | Voto ottenuto          | Microservizio Compiti      |

---

#### `GET /api/v1/reports/student/{studentId}/exams`
**Parametri**
- `studentId`: ID univoco dello studente (proveniente dal microservizio Gestione Utenti)

**Output**
```json
[
  {
    "examId": "EX123",
    "course": "Basi di Dati",
    "grade": 30
  }
]
```

| Campo     | Significato              | Origine                   |
|-----------|---------------------------|---------------------------|
| `examId`  | ID univoco esame          | Microservizio Esami       |
| `course`  | Nome del corso            | Microservizio Corsi       |
| `grade`   | Voto ottenuto             | Microservizio Esami       |

---

#### `GET /api/v1/reports/student/{studentId}/summary`
**Parametri**
- `studentId`: ID univoco dello studente (proveniente dal microservizio Gestione Utenti)

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

| Campo                 | Significato                                  |
|-----------------------|-----------------------------------------------|
| `studentId`           | ID dello studente                             |
| `studentName`         | Nome completo dello studente                  |
| `studentEmail`        | Email istituzionale                           |
| `enrolledCourses`     | Numero di corsi frequentati                   |
| `completedExams`      | Numero di esami completati                    |
| `averageGrade`        | Media voti                                    |
| `attendanceRate`      | Percentuale media di presenza                 |
| `assignmentsSubmitted`| Numero di compiti consegnati                  |
| `assignmentsTotal`    | Numero totale di compiti assegnati            |
| `reportGeneratedAt`   | Timestamp di generazione del report           |

---

#### `POST /api/v1/reports/student`
**Input**
```json
{
  "studentId": "12345",
  "startDate": "01-09-2024",
  "endDate": "31-03-2025",
  "format": "pdf"
}
```

| Campo       | Significato                               |
|-------------|-------------------------------------------|
| `studentId` | ID dello studente (da Gestione Utenti)    |
| `startDate` | Data inizio periodo (formato DD-MM-YYYY)  |
| `endDate`   | Data fine periodo (formato DD-MM-YYYY)    |
| `format`    | Formato del report (`pdf`, `csv`, `json`) |

**Output**
```json
"Exported report for student 12345 in PDF format"
```

---

### Report Corso

#### `GET /api/v1/reports/course/{courseId}/grades`
**Parametri**
- `courseId`: Codice del corso (da Gestione Corsi)

**Output**
```json
[
  {
    "studentId": "123",
    "grade": 27
  }
]
```

| Campo       | Significato                     |
|-------------|----------------------------------|
| `studentId` | ID dello studente                |
| `grade`     | Voto ricevuto                    |

---

#### `GET /api/v1/reports/course/{courseId}/attendance`
**Parametri**
- `courseId`: Codice del corso (da Gestione Corsi)

**Output**
```json
[
  {
    "studentId": "123",
    "attendanceRate": 92.5
  }
]
```

| Campo          | Significato                     |
|----------------|----------------------------------|
| `studentId`    | ID dello studente                |
| `attendanceRate` | Percentuale di frequenza       |

---

#### `GET /api/v1/reports/course/{courseId}/assignments`
**Parametri**
- `courseId`: Codice del corso (da Gestione Corsi)

**Output**
```json
[
  {
    "assignment": "Progetto finale",
    "completionRate": 87.0
  }
]
```

| Campo           | Significato                         |
|------------------|--------------------------------------|
| `assignment`     | Nome dell'assegnazione              |
| `completionRate` | Percentuale di completamento        |

---

#### `GET /api/v1/reports/course/{courseId}/summary`
**Parametri**
- `courseId`: Codice del corso (da Gestione Corsi)

**Output**
```json
{
  "courseId": "INF001",
  "courseName": "Basi di Dati",
  "enrolledStudents": 120,
  "averageGrade": 26.7,
  "averageAttendance": 88.9,
  "assignmentsGiven": 6,
  "reportGeneratedAt": "2025-05-30T12:00:00"
}
```

| Campo              | Significato                            |
|---------------------|-----------------------------------------|
| `courseId`          | ID del corso                            |
| `courseName`        | Nome del corso                          |
| `enrolledStudents`  | Numero studenti iscritti                |
| `averageGrade`      | Media voti                              |
| `averageAttendance` | Media presenze                          |
| `assignmentsGiven`  | Numero totale di compiti assegnati      |
| `reportGeneratedAt` | Timestamp generazione del report        |

---

### Report Docente

#### `POST /api/v1/reports/teacher`
**Input**
```json
{
  "teacherId": "T9876",
  "startDate": "2024-10-01",
  "endDate": "2025-03-31",
  "format": "csv"
}
```

| Campo       | Significato                                |
|-------------|---------------------------------------------|
| `teacherId` | ID docente (da Gestione Utenti)            |
| `startDate` | Inizio intervallo (formato YYYY-MM-DD)     |
| `endDate`   | Fine intervallo (formato YYYY-MM-DD)       |
| `format`    | Formato desiderato (`pdf`, `csv`, `json`)  |

**Output**
```json
"Exported report for teacher T9876 in CSV format"
```

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
