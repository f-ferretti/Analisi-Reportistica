# Microservizio Analisi e Reportistica

## Descrizione del Progetto

Il microservizio **Analisi e Reportistica** è responsabile della generazione di report di base sull'attività della piattaforma universitaria. Fornisce informazioni aggregate e analisi dei dati accademici per supportare decisioni gestionali e monitorare le performance di studenti, corsi e docenti.

## Funzionalità Principali

Il microservizio implementa le seguenti funzionalità per gli utenti con ruolo **Amministrativo**:

### 1. Report Attività di uno Studente

- `GET /api/reports/student/{studentId}/courses`  
  ➤ Restituisce l'elenco dei corsi frequentati dallo studente.

- `GET /api/reports/student/{studentId}/assignments`  
  ➤ Storico dei compiti consegnati, con stato e voto.

- `GET /api/reports/student/{studentId}/exams`  
  ➤ Esami sostenuti con risultati e dettagli.

- `GET /api/reports/student/{studentId}/summary`  
  ➤ Report aggregato con media voti, corsi, compiti e presenze.

- `POST /api/reports/student`  
  ➤ Genera un report personalizzato per uno studente con filtri su date e formato.

### 2. Report Performance degli Studenti in un Corso

- `GET /api/reports/course/{courseId}/grades`  
  ➤ Statistiche sui voti degli studenti.

- `GET /api/reports/course/{courseId}/attendance`  
  ➤ Percentuali di frequenza.

- `GET /api/reports/course/{courseId}/assignments`  
  ➤ Percentuali di consegna e punteggi medi.

- `GET /api/reports/course/{courseId}/summary`  
  ➤ Vista complessiva della performance del corso.

- `POST /api/reports/course`  
  ➤ Genera un report personalizzato per un corso con opzioni avanzate.

### 3. Report Valutazioni di un Docente

- `GET /api/reports/teacher/{teacherId}/given-grades`  
  ➤ Statistiche sui voti assegnati dal docente.

- `GET /api/reports/teacher/{teacherId}/feedbacks`  
  ➤ Aggregazione dei feedback ricevuti.

- `GET /api/reports/teacher/{teacherId}/summary`  
  ➤ Report finale sul docente.

- `POST /api/reports/export`  
  ➤ Esporta o salva un report generato in formato specifico (es. PDF).

## API Endpoints

### Report Studente

#### GET /api/reports/student/{studentId}/courses
#### GET /api/reports/student/{studentId}/assignments
#### GET /api/reports/student/{studentId}/exams
#### GET /api/reports/student/{studentId}/summary
#### POST /api/reports/student

```json
{
  "studentId": "12345",
  "startDate": "2024-09-01",
  "endDate": "2025-03-31",
  "format": "pdf"
}
```

### Report Corso

#### GET /api/reports/course/{courseId}/grades
#### GET /api/reports/course/{courseId}/attendance
#### GET /api/reports/course/{courseId}/assignments
#### GET /api/reports/course/{courseId}/summary
#### POST /api/reports/course

```json
{
  "courseId": "CS101",
  "format": "json",
  "includeDetails": true
}
```

### Report Docente

#### GET /api/reports/teacher/{teacherId}/given-grades
#### GET /api/reports/teacher/{teacherId}/feedbacks
#### GET /api/reports/teacher/{teacherId}/summary
#### POST /api/reports/export

```json
{
  "reportType": "teacher",
  "targetId": "DOC001",
  "format": "pdf",
  "notify": true
}
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
