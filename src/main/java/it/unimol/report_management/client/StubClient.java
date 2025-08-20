package it.unimol.report_management.client;

import it.unimol.report_management.dto.student.ExamResultDTO;
import it.unimol.report_management.dto.student.PlanItemDTO;
import it.unimol.report_management.dto.student.StudentDTO;
import it.unimol.report_management.exception.ResourceNotFoundException;
import it.unimol.report_management.exception.UpstreamBadDataException;
import it.unimol.report_management.exception.UpstreamTimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
public class StubClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StubClient(RestTemplate restTemplate,
                      @Value("${stub.base-url:http://localhost:8000}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    private RuntimeException translate(RestClientException e, String op) {
        String msg = e.getMessage() != null ? e.getMessage() : "";
        String l = msg.toLowerCase();
        if (l.contains("timed out") || l.contains("timeout")) {
            return new UpstreamTimeoutException("Timeout dallo stub durante " + op);
        }
        return new UpstreamBadDataException("Errore nel chiamare lo stub durante " + op + ": " + msg);
    }

    // ================== STUDENTI ==================

    public StudentDTO getStudent(String matricola) {
        String url = baseUrl + "/stub/v1/students/" + matricola;
        try {
            StudentDTO body = restTemplate.getForObject(url, StudentDTO.class);
            if (body == null) {
                throw new ResourceNotFoundException("Studente " + matricola + " non trovato nello stub");
            }
            return body;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Studente " + matricola + " non trovato nello stub");
        } catch (RestClientException e) { throw translate(e, "getStudent"); }
    }

    public List<ExamResultDTO> getStudentExams(String matricola) {
        String url = baseUrl + "/stub/v1/students/" + matricola + "/exams";
        try {
            ResponseEntity<List<ExamResultDTO>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ExamResultDTO>>() {});
            List<ExamResultDTO> body = resp.getBody();
            return body != null ? body : Collections.emptyList();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Esami per studente " + matricola + " non trovati");
        } catch (RestClientException e) { throw translate(e, "getStudentExams"); }
    }

    /**
     * Mappo -> PlanItemDTO {codice, nome, cfu, annoCorso, obbligatorio}.
     */
    public List<PlanItemDTO> getStudentPlan(String matricola) {
        String url = baseUrl + "/stub/v1/students/" + matricola + "/plan";
        try {
            ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> raw = resp.getBody();
            if (raw == null) return Collections.emptyList();

            List<PlanItemDTO> mapped = new ArrayList<>();
            for (Map<String, Object> m : raw) {
                if (m == null) continue;
                PlanItemDTO dto = new PlanItemDTO();
                dto.setCodice(asString(m.get("codiceCorso")));
                dto.setNome(asString(m.get("nome")));
                dto.setCfu(asInt(m.get("cfu")));
                dto.setAnnoCorso(asInteger(m.get("anno")));
                Object obbl = m.get("obbligatorio");
                dto.setObbligatorio(obbl instanceof Boolean ? (Boolean) obbl : null);
                mapped.add(dto);
            }
            return mapped;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Piano di studio per studente " + matricola + " non trovato");
        } catch (RestClientException e) { throw translate(e, "getStudentPlan"); }
    }

    public Map<String, Object> getGraduationFactors(String matricola, Integer aa) {
        String url = baseUrl + "/stub/v1/students/" + matricola + "/fattori-laurea";
        if (aa != null) url += "?aa=" + aa;
        try {
            ResponseEntity<Map<String,Object>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String,Object>>() {});
            Map<String,Object> body = resp.getBody();
            if (body == null) throw new UpstreamBadDataException("Fattori laurea vuoti dallo stub");
            return body;
        } catch (HttpClientErrorException.NotFound e) {
            String body = e.getResponseBodyAsString();
            throw new ResourceNotFoundException(body != null && !body.isBlank() ? body : "Fattori laurea non trovati");
        } catch (RestClientException e) { throw translate(e, "getGraduationFactors"); }
    }

    // ================== CORSI ==================

    /**
     * Voti corso per Anno Accademico.
     * Mappo su ExamResultDTO popolando solo i campi disponibili.
     */
    public List<ExamResultDTO> getCourseGradesToExamDTO(String courseCode, Integer aa) {
        String url = baseUrl + "/stub/v1/courses/" + courseCode + "/grades";
        if (aa != null) url += "?aa=" + aa;
        try {
            ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> raw = resp.getBody();
            if (raw == null) return Collections.emptyList();

            List<ExamResultDTO> mapped = new ArrayList<>();
            for (Map<String, Object> m : raw) {
                if (m == null) continue;
                ExamResultDTO dto = new ExamResultDTO();
                dto.setCodiceCorso(courseCode); // dal path
                dto.setVoto(asInt(m.get("voto")));
                dto.setLode(asBool(m.get("lode")));
                dto.setData(asDate(m.get("data")));
                // nomeCorso/cfu/aa* non presenti nello stub -> lascio default
                mapped.add(dto);
            }
            return mapped;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Voti per corso " + courseCode + " non trovati");
        } catch (RestClientException e) { throw translate(e, "getCourseGrades"); }
    }

    // === alias per compatibilità con codice esistente (es. TeacherReportServiceImpl) ===
    public List<ExamResultDTO> getCourseGrades(String courseCode, Integer aa) {
        return getCourseGradesToExamDTO(courseCode, aa);
    }
    public List<ExamResultDTO> getCourseGrades(String courseCode) {
        return getCourseGradesToExamDTO(courseCode, null);
    }

    /**
     * Iscritti corso per anno .
     */
    public List<Map<String, Integer>> getCourseEnrollmentsByYear(String courseCode) {
        String url = baseUrl + "/stub/v1/courses/" + courseCode + "/enrollments";
        try {
            ResponseEntity<List<Map<String, Object>>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> raw = resp.getBody();
            if (raw == null) return Collections.emptyList();

            return raw.stream().filter(Objects::nonNull).map(m -> {
                Integer anno = asInteger(m.get("anno"));
                Integer iscritti = asInteger(m.get("iscritti"));
                Map<String, Integer> out = new LinkedHashMap<>();
                out.put("anno", anno != null ? anno : 0);
                out.put("iscritti", iscritti != null ? iscritti : 0);
                return out;
            }).collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Iscritti per corso " + courseCode + " non trovati");
        } catch (RestClientException e) { throw translate(e, "getEnrollments"); }
    }

    // ================== DOCENTI ==================

    public List<Map<String,String>> getTeacherTeachings(String teacherId, Integer aa) {
        String url = baseUrl + "/stub/v1/teachers/" + teacherId + "/teachings";
        if (aa != null) url += "?aa=" + aa;
        try {
            ResponseEntity<List<Map<String,String>>> resp = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String,String>>>() {});
            List<Map<String,String>> body = resp.getBody();
            return body != null ? body : Collections.emptyList();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Insegnamenti per docente " + teacherId + " non trovati");
        } catch (RestClientException e) { throw translate(e, "getTeacherTeachings"); }
    }

    // ================== helpers ==================

    private static String asString(Object o) { return o == null ? null : String.valueOf(o); }
    private static int asInt(Object o) {
        if (o instanceof Number n) return n.intValue();
        try { return o != null ? Integer.parseInt(String.valueOf(o)) : 0; } catch (Exception e) { return 0; }
    }
    private static Integer asInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.valueOf(String.valueOf(o)); } catch (Exception e) { return null; }
    }
    private static boolean asBool(Object o) {
        if (o instanceof Boolean b) return b;
        if (o == null) return false;
        return "true".equalsIgnoreCase(String.valueOf(o));
    }
    private static LocalDate asDate(Object o) {
        if (o == null) return null;
        try { return LocalDate.parse(String.valueOf(o)); } catch (Exception e) { return null; }
    }
}