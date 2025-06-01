package it.unimol.report_management.service.impl;

import it.unimol.report_management.client.*;
import it.unimol.report_management.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final UtentiClient utentiClient;
    private final EsamiClient esamiClient;
    private final CorsiClient corsiClient;
    private final CompitiClient compitiClient;
    private final PresenzeClient presenzeClient;
    private final FeedbackClient feedbackClient;

    public ReportServiceImpl(UtentiClient utentiClient, EsamiClient esamiClient, CorsiClient corsiClient,
                             CompitiClient compitiClient, PresenzeClient presenzeClient, FeedbackClient feedbackClient) {
        this.utentiClient = utentiClient;
        this.esamiClient = esamiClient;
        this.corsiClient = corsiClient;
        this.compitiClient = compitiClient;
        this.presenzeClient = presenzeClient;
        this.feedbackClient = feedbackClient;
    }

    private ResponseEntity<?> mock(String description) {
        return ResponseEntity.ok(Map.of("mock", true, "description", description));
    }

    public ResponseEntity<?> getStudentActivity(String studentId, String startDate, String endDate, String format) {
        // TODO: usare compitiClient e presenzeClient
        return mock("Student Activity Report for " + studentId);
    }

    public ResponseEntity<?> getStudentGrades(String studentId, String format) {
        // TODO: usare esamiClient per voti dello studente
        return mock("Student Grades Report for " + studentId);
    }

    public ResponseEntity<?> getStudentProgress(String studentId) {
        // TODO: calcolare da presenzeClient
        return mock("Student Progress");
    }

    public ResponseEntity<?> getStudentAverage(String studentId) {
        // TODO: media da esamiClient
        return mock("Student Average");
    }

    public ResponseEntity<?> getStudentCompletionRate(String studentId) {
        // TODO: completamento compiti o esami
        return mock("Student Completion Rate");
    }

    public ResponseEntity<?> getCourseAverage(String courseId) {
        // TODO: media voti studenti corso da esamiClient
        return mock("Course Average");
    }

    public ResponseEntity<?> getCourseGradeDistribution(String courseId) {
        // TODO: istogramma voti da esamiClient
        return mock("Course Grade Distribution");
    }

    public ResponseEntity<?> getCourseCompletionRate(String courseId) {
        // TODO: % studenti che hanno completato corso
        return mock("Course Completion Rate");
    }

    public ResponseEntity<?> getTeacherRatings(String teacherId) {
        // TODO: media feedback da feedbackClient
        return mock("Teacher Ratings");
    }

    public ResponseEntity<?> getTeacherAverage(String teacherId) {
        // TODO: media dei voti assegnati dal docente
        return mock("Teacher Average");
    }

    public ResponseEntity<?> getTeacherFeedback(String teacherId) {
        // TODO: lista feedback da feedbackClient
        return mock("Teacher Feedback");
    }

    public ResponseEntity<?> getStudentPerformanceOverTime(String studentId, String startDate, String endDate, String format) {
        // TODO: andamento voti + compiti in timeline
        return mock("Student Performance Over Time");
    }

    public ResponseEntity<?> getCoursePerformanceOverTime(String courseId, String startDate, String endDate, String format) {
        // TODO: performance media studenti nel tempo
        return mock("Course Performance Over Time");
    }

    public ResponseEntity<?> getTeacherPerformanceOverTime(String teacherId, String startDate, String endDate, String format) {
        // TODO: performance valutazioni nel tempo
        return mock("Teacher Performance Over Time");
    }

    public ResponseEntity<?> getGlobalSummary() {
        // TODO: aggregazione di più metriche
        return mock("Global Summary Report");
    }
}