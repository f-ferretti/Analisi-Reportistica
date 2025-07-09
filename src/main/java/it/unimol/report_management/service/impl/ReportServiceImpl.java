// src/main/java/it/unimol/report_management/service/impl/ReportServiceImpl.java
package it.unimol.report_management.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimol.report_management.pdf.PdfGenerator;
import it.unimol.report_management.service.ReportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
public class ReportServiceImpl implements ReportService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stub.base-url}")
    private String stubBaseUrl;

    // === DOCENTI ===
    @Override
    public ResponseEntity<?> getTeacherFeedback(int teacherId, String startDate, String endDate, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/teachers/" + teacherId + "/feedback")
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getTeacherRatings(int teacherId, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/teachers/" + teacherId + "/ratings").toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getTeacherAverage(int teacherId, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/teachers/" + teacherId + "/average").toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getTeacherPerformance(int teacherId, String startDate, String endDate, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/teachers/" + teacherId + "/performance-over-time")
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .toUriString();
        return forwardRequest(url, format);
    }

    // === STUDENTI ===
    @Override
    public ResponseEntity<?> getStudentGrades(int studentId, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/students/" + studentId + "/grades").toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getStudentProgress(int studentId, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/students/" + studentId + "/progress").toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getStudentAverage(int studentId, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/students/" + studentId + "/average").toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getStudentCompletionRate(int studentId, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/students/" + studentId + "/completion-rate").toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getStudentPerformance(int studentId, String startDate, String endDate, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/students/" + studentId + "/performance-over-time")
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getStudentActivity(int studentId, String startDate, String endDate, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/students/" + studentId + "/activity")
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .toUriString();
        return forwardRequest(url, format);
    }

    // === CORSI ===
    @Override
    public ResponseEntity<?> getCourseAverage(int courseId, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/courses/" + courseId + "/average").toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getCourseDistribution(int courseId, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/courses/" + courseId + "/distribution").toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getCourseCompletionRate(int courseId, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/courses/" + courseId + "/completion-rate").toUriString();
        return forwardRequest(url, format);
    }

    @Override
    public ResponseEntity<?> getCoursePerformance(int courseId, String startDate, String endDate, String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/courses/" + courseId + "/performance-over-time")
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .toUriString();
        return forwardRequest(url, format);
    }

    // === RIEPILOGO GLOBALE ===
    @Override
    public ResponseEntity<?> getGlobalSummary(String format) {
        String url = UriComponentsBuilder.fromHttpUrl(stubBaseUrl + "/summary").toUriString();
        return forwardRequest(url, format);
    }

    /**
     * Forward della richiesta e generazione PDF con titolo in italiano.
     */
    private ResponseEntity<?> forwardRequest(String url, String format) {
        try {
            // Chiamata al downstream stub
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String jsonBody = response.getBody();
            if ("pdf".equalsIgnoreCase(format)) {
                // Deserializza JSON in lista di mappe
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonBody);
                List<Map<String,Object>> dataList = new ArrayList<>();
                if (root.isArray()) {
                    for (JsonNode el : root) {
                        if (el.isObject()) {
                            dataList.add(mapper.convertValue(el, new TypeReference<Map<String,Object>>(){}));
                        } else {
                            Map<String,Object> m = new LinkedHashMap<>();
                            m.put("valore", el.asText());
                            dataList.add(m);
                        }
                    }
                } else if (root.isObject()) {
                    dataList.add(mapper.convertValue(root, new TypeReference<Map<String,Object>>(){}));
                }
                // Genera titolo in italiano
                String titolo = extractItalianTitle(url);
                byte[] pdf = PdfGenerator.createTablePdf(titolo, dataList);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(pdf);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(jsonBody);
            }
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }

    /**
     * Estrae e traduce in italiano il titolo dal path URL.
     */
    private String extractItalianTitle(String url) {
        String path = url.substring(stubBaseUrl.length());
        if (path.contains("?")) path = path.substring(0, path.indexOf("?"));
        String[] parts = path.split("/");
        if (parts.length >= 4) {
            String entity = parts[1];
            String id     = parts[2];
            String report = parts[3];
            Map<String,String> itEntities = new HashMap<>();
            itEntities.put("teachers", "Docente");
            itEntities.put("students", "Studente");
            itEntities.put("courses",  "Corso");
            Map<String,String> itReports = new HashMap<>();
            itReports.put("feedback", "Feedback");
            itReports.put("ratings",  "Valutazioni");
            itReports.put("average",  "Media");
            itReports.put("performance-over-time", "Andamento");
            itReports.put("grades", "Voti");
            itReports.put("progress", "Progresso");
            itReports.put("completion-rate", "Completamento");
            itReports.put("distribution", "Distribuzione");
            itReports.put("activity", "Attività");
            itReports.put("summary", "Riepilogo");
            String itEntity = itEntities.getOrDefault(entity, entity);
            String itReport = itReports.getOrDefault(report, report);
            return String.format("%s %s: %s", itEntity, id, itReport);
        }
        // Fallback generico
        return path.replace('/', ' ');
    }
}