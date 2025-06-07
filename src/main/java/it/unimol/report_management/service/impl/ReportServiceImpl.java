package it.unimol.report_management.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import it.unimol.report_management.model.ReportCache;
import it.unimol.report_management.repository.ReportCacheRepository;
import it.unimol.report_management.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
    private final ReportCacheRepository cacheRepo;

    public ReportServiceImpl(ReportCacheRepository cacheRepo) {
        this.cacheRepo = cacheRepo;
    }

    // ========== PDF ==========
    @Override
    public ResponseEntity<byte[]> generateStudentActivityPdf(String studentId, String startDate, String endDate, String format) {
        String params = toParams(startDate, endDate);
        return getOrGeneratePdf("student_activity", studentId, params,
                new String[][]{{"Lezione","Data","Stato"},{"Reti","2025-03-15","Presente"}},
                "Attività Studente", startDate, endDate);
    }
    @Override public ResponseEntity<byte[]> generateStudentGradesPdf(String studentId, String format) {
        return getOrGeneratePdf("student_grades", studentId, "", new String[][]{{"Corso","Voto"},{"Basi di Dati","28"}},"Voti Studente", null,null);
    }
    @Override public ResponseEntity<byte[]> generateStudentProgressPdf(String studentId) {
        return getOrGeneratePdf("student_progress", studentId, "", new String[][]{{"CFU Completati","120"}},"Progresso Studente",null,null);
    }
    @Override public ResponseEntity<byte[]> generateStudentAveragePdf(String studentId) {
        return getOrGeneratePdf("student_average", studentId, "", new String[][]{{"Media","27.5"}},"Media Studente",null,null);
    }
    @Override public ResponseEntity<byte[]> generateStudentCompletionRatePdf(String studentId) {
        return getOrGeneratePdf("student_completion_rate", studentId, "", new String[][]{{"Completamento","92%"}},"Completamento Studente",null,null);
    }
    @Override public ResponseEntity<byte[]> generateCourseAveragePdf(String courseId) {
        return getOrGeneratePdf("course_average", courseId, "", new String[][]{{"Media","26.7"}},"Media Corso",null,null);
    }
    @Override public ResponseEntity<byte[]> generateCourseGradeDistributionPdf(String courseId) {
        return getOrGeneratePdf("course_grade_distribution", courseId, "", new String[][]{{"18-20","5"}},"Distribuzione Corso",null,null);
    }
    @Override public ResponseEntity<byte[]> generateCourseCompletionRatePdf(String courseId) {
        return getOrGeneratePdf("course_completion_rate", courseId, "", new String[][]{{"Completamento","85%"}},"Completamento Corso",null,null);
    }
    @Override public ResponseEntity<byte[]> generateTeacherRatingsPdf(String teacherId) {
        return getOrGeneratePdf("teacher_ratings", teacherId, "", new String[][]{{"Studente","Voto"}},"Valutazioni Docente",null,null);
    }
    @Override public ResponseEntity<byte[]> generateTeacherAveragePdf(String teacherId) {
        return getOrGeneratePdf("teacher_average", teacherId, "", new String[][]{{"Media","28.1"}},"Media Docente",null,null);
    }
    @Override public ResponseEntity<byte[]> generateTeacherFeedbackPdf(String teacherId) {
        return getOrGeneratePdf("teacher_feedback", teacherId, "", new String[][]{{"Studente","Feedback"}},"Feedback Docente",null,null);
    }
    @Override public ResponseEntity<byte[]> generateStudentPerformanceOverTimePdf(String studentId, String startDate, String endDate, String format) {
        return getOrGeneratePdf("student_performance_over_time", studentId, toParams(startDate,endDate),
                new String[][]{{"2024-09","26"}},"Performance Studente",startDate,endDate);
    }
    @Override public ResponseEntity<byte[]> generateCoursePerformanceOverTimePdf(String courseId, String startDate, String endDate, String format) {
        return getOrGeneratePdf("course_performance_over_time", courseId, toParams(startDate,endDate), new String[][]{{"2024-10","25"}},"Performance Corso",startDate,endDate);
    }
    @Override public ResponseEntity<byte[]> generateTeacherPerformanceOverTimePdf(String teacherId, String startDate, String endDate, String format) {
        return getOrGeneratePdf("teacher_performance_over_time", teacherId, toParams(startDate,endDate), new String[][]{{"2024-10","28"}},"Performance Docente",startDate,endDate);
    }
    @Override
    public ResponseEntity<byte[]> generateGlobalSummaryPdf() {
        String placeholder = "PDF non ancora implementato";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=global_summary.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(placeholder.getBytes(StandardCharsets.UTF_8));
    }

    // ========== JSON ==========
    @Override public ResponseEntity<?> getStudentActivity(String studentId, String startDate, String endDate, String format) {
        return getOrGenerateJson("student_activity", studentId, toParams(startDate,endDate), Map.of("exams",5));
    }
    @Override public ResponseEntity<?> getStudentGrades(String studentId, String format) {
        return getOrGenerateJson("student_grades", studentId, "", Map.of("grades", new int[]{28,30,27}));
    }
    @Override public ResponseEntity<?> getStudentProgress(String studentId) {
        return getOrGenerateJson("student_progress", studentId, "", Map.of("progress",82.5));
    }
    @Override public ResponseEntity<?> getStudentAverage(String studentId) {
        return getOrGenerateJson("student_average", studentId, "", Map.of("average",27.3));
    }
    @Override public ResponseEntity<?> getStudentCompletionRate(String studentId) {
        return getOrGenerateJson("student_completion_rate", studentId, "", Map.of("rate",0.89));
    }
    @Override public ResponseEntity<?> getCourseAverage(String courseId) {
        return getOrGenerateJson("course_average", courseId, "", Map.of("average",26.7));
    }
    @Override public ResponseEntity<?> getCourseGradeDistribution(String courseId) {
        return getOrGenerateJson("course_grade_distribution", courseId, "", Map.of("dist", Map.of("18-20",5)));
    }
    @Override public ResponseEntity<?> getCourseCompletionRate(String courseId) {
        return getOrGenerateJson("course_completion_rate", courseId, "", Map.of("rate",0.81));
    }
    @Override public ResponseEntity<?> getTeacherRatings(String teacherId) {
        return getOrGenerateJson("teacher_ratings", teacherId, "", Map.of("ratings", new int[]{5,4,3}));
    }
    @Override public ResponseEntity<?> getTeacherAverage(String teacherId) {
        return getOrGenerateJson("teacher_average", teacherId, "", Map.of("average",28.1));
    }
    @Override public ResponseEntity<?> getTeacherFeedback(String teacherId) {
        return getOrGenerateJson("teacher_feedback", teacherId, "", Map.of("feedback", new String[]{"Ottimo","Chiaro"}));
    }
    @Override public ResponseEntity<?> getStudentPerformanceOverTime(String studentId, String startDate, String endDate, String format) {
        return getOrGenerateJson("student_performance_over_time", studentId, toParams(startDate,endDate), Map.of("monthly", Map.of("2024-09",26)));
    }
    @Override public ResponseEntity<?> getCoursePerformanceOverTime(String courseId, String startDate, String endDate, String format) {
        return getOrGenerateJson("course_performance_over_time", courseId, toParams(startDate,endDate), Map.of("monthly", Map.of("2024-10",25)));
    }
    @Override public ResponseEntity<?> getTeacherPerformanceOverTime(String teacherId, String startDate, String endDate, String format) {
        return getOrGenerateJson("teacher_performance_over_time", teacherId, toParams(startDate,endDate), Map.of("monthly", Map.of("2024-10",28)));
    }
    @Override
    public ResponseEntity<?> getGlobalSummary() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalReports", 0);
        result.put("generatedAt", System.currentTimeMillis());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    // ========== NUOVI METODI PER INTERFACCIA ==========
    @Override
    public Map<String, Object> getGlobalSummaryMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalReports", 0);
        result.put("generatedAt", System.currentTimeMillis());
        return result;
    }

    @Override
    public byte[] generateGlobalSummaryPdfRaw() {
        String placeholder = "PDF non ancora implementato";
        return placeholder.getBytes(StandardCharsets.UTF_8);
    }

    // ===== utils =====
    private <T> ResponseEntity<T> getOrGenerateJson(
            String type, String id, String params, T body) {
        String fmt = "json";
        Optional<ReportCache> c = cacheRepo.findByReportTypeAndTargetIdAndParametersAndFormat(type,id,params,fmt);
        if (c.isPresent()) return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body((T)c.get().getReportDataAsJson());
        cacheRepo.save(ReportCache.builder().reportType(type).targetId(id).parameters(params).format(fmt)
                .reportData(body.toString().getBytes(StandardCharsets.UTF_8)).generatedAt(LocalDateTime.now()).build());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    private ResponseEntity<byte[]> getOrGeneratePdf(
            String type, String id, String params, String[][] rows,
            String title, String start, String end) {
        String fmt = "pdf";
        Optional<ReportCache> c = cacheRepo.findByReportTypeAndTargetIdAndParametersAndFormat(type,id,params,fmt);
        if (c.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(c.get().getReportData());
        }
        byte[] pdf = buildPdf(title,id,start,end,rows);
        cacheRepo.save(ReportCache.builder().reportType(type).targetId(id).parameters(params).format(fmt)
                .reportData(pdf).generatedAt(LocalDateTime.now()).build());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private byte[] buildPdf(String title, String id, String start, String end, String[][] data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc,out);
            doc.open();
            doc.add(new Paragraph(title));
            doc.add(new Paragraph("ID: "+id));
            if (start!=null) doc.add(new Paragraph("Start: "+start));
            if (end!=null) doc.add(new Paragraph("End: "+end));
            doc.add(Chunk.NEWLINE);
            PdfPTable table = new PdfPTable(data[0].length);
            Arrays.stream(data).forEach(r-> Arrays.stream(r).forEach(table::addCell));
            doc.add(table);
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("PDF generation failed",e);
            return new byte[0];
        }
    }

    private String toParams(String s, String e) {
        return String.format("{\"start\":\"%s\",\"end\":\"%s\"}", s,e);
    }
}