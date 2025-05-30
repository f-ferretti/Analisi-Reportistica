package it.unimol.report_management.service.impl;

import it.unimol.report_management.client.*;
import it.unimol.report_management.dto.*;
import it.unimol.report_management.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired private UtentiClient utentiClient;
    @Autowired private EsamiClient esamiClient;
    @Autowired private CorsiClient corsiClient;
    @Autowired private CompitiClient compitiClient;
    @Autowired private PresenzeClient presenzeClient;
    @Autowired private FeedbackClient feedbackClient;

    @Override
    public StudentReportDTO generateStudentReport(String studentId) {
        Map<String, Object> studente = utentiClient.getUtenteById(studentId);
        String nomeCompleto = studente.get("nome") + " " + studente.get("cognome");
        String email = (String) studente.get("email");

        return new StudentReportDTO(
                studentId,
                nomeCompleto,
                email,
                6, 12, 27.3, 89.5, 10, 12,
                LocalDateTime.now()
        );
    }

    @Override
    public List<Map<String, String>> getCoursesByStudent(String studentId) {
        List<String> corsiIds = corsiClient.getCorsiDocente(studentId); // supponiamo lo studente abbia ID uguale anche nel contesto corsi
        return corsiIds.stream().map(id -> Map.of("courseId", id, "name", "Mock Corso")).toList();
    }

    @Override
    public List<Map<String, Object>> getAssignmentsByStudent(String studentId) {
        return compitiClient.getCompitiStudente(studentId);
    }

    @Override
    public List<Map<String, Object>> getExamsByStudent(String studentId) {
        return esamiClient.getEsamiSostenuti(studentId);
    }

    @Override
    public CourseReportDTO generateCourseReport(String courseId) {
        Map<String, Object> corso = corsiClient.getCorsoById(courseId);
        List<Map<String, Object>> voti = esamiClient.getEsitiEsameCorso(courseId);
        List<Map<String, Object>> compiti = compitiClient.getCompitiCorso(courseId);
        List<Map<String, Object>> presenze = presenzeClient.getPresenzeCorso(courseId);

        return new CourseReportDTO(
                courseId,
                (String) corso.get("nome"),
                (String) corso.get("id"),
                voti.size(),
                25.0,
                78.0,
                82.0,
                88.0,
                LocalDateTime.now()
        );
    }

    @Override
    public List<Map<String, Object>> getGradesByCourse(String courseId) {
        return esamiClient.getEsitiEsameCorso(courseId);
    }

    @Override
    public List<Map<String, Object>> getAttendanceByCourse(String courseId) {
        return presenzeClient.getPresenzeCorso(courseId);
    }

    @Override
    public List<Map<String, Object>> getAssignmentsByCourse(String courseId) {
        return compitiClient.getCompitiCorso(courseId);
    }

    @Override
    public TeacherReportDTO generateTeacherReport(String teacherId) {
        Map<String, Object> docente = utentiClient.getUtenteById(teacherId);
        Map<String, Object> feedback = feedbackClient.getFeedbackDocente(teacherId);
        List<String> corsi = corsiClient.getCorsiDocente(teacherId);

        String nomeCompleto = docente.get("nome") + " " + docente.get("cognome");
        String email = (String) docente.get("email");
        double rating = (double) feedback.get("media");

        return new TeacherReportDTO(
                teacherId,
                nomeCompleto,
                email,
                corsi.size(),
                120,
                rating,
                85.0,
                LocalDateTime.now()
        );
    }

    @Override
    public List<Map<String, Object>> getGradesGivenByTeacher(String teacherId) {
        return List.of(Map.of("course", "Basi di Dati", "avgGrade", 28.3)); // placeholder
    }

    @Override
    public List<Map<String, Object>> getFeedbacksForTeacher(String teacherId) {
        Map<String, Object> feedback = feedbackClient.getFeedbackDocente(teacherId);
        return List.of(feedback);
    }

    @Override
    public StudentReportDTO generateStudentReportFromRequest(ReportRequestDTO requestDTO) {
        return generateStudentReport(requestDTO.getTargetId());
    }

    @Override
    public CourseReportDTO generateCourseReportFromRequest(ReportRequestDTO requestDTO) {
        return generateCourseReport(requestDTO.getTargetId());
    }

    @Override
    public TeacherReportDTO generateTeacherReportFromRequest(ReportRequestDTO requestDTO) {
        return generateTeacherReport(requestDTO.getTargetId());
    }

    @Override
    public String exportReport(ReportRequestDTO requestDTO) {
        return "Exported report of type '" + requestDTO.getReportType() + "' for " + requestDTO.getTargetId();
    }
}