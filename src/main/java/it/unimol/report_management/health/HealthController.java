package it.unimol.report_management.health;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Health endpoint applicativo.
 * GET /api/v1/health
 */
@RestController
@Tag(name = "Health", description = "Stato del microservizio e dipendenze")
public class HealthController {

    private final JdbcTemplate jdbcTemplate; // può essere null se DB non configurato
    private final RestTemplate restTemplate;

    @Value("${app.version:unknown}")
    private String version;

    @Value("${app.build-time:}")
    private String buildTime;

    @Value("${stub.base-url:http://localhost:8000}")
    private String stubBaseUrl;

    @Autowired
    public HealthController(RestTemplate restTemplate, @Autowired(required = false) JdbcTemplate jdbcTemplate) {
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Operation(summary = "Stato applicativo", description = "Ritorna stato UP/DOWN e stato delle dipendenze (database, stub, pdfEngine)")
    @GetMapping(value = "/api/v1/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public HealthResponse health() {
        Map<String, String> deps = new HashMap<>();
        boolean up = true;

        try {
            if (jdbcTemplate != null) {
                Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                deps.put("database", (one != null && one == 1) ? "UP" : "DOWN");
                if (one == null || one != 1) up = false;
            } else {
                deps.put("database", "UNKNOWN");
            }
        } catch (Exception e) {
            deps.put("database", "DOWN");
            up = false;
        }

        try {
            StopWatch sw = new StopWatch();
            sw.start();
            String pong = restTemplate.getForObject(stubBaseUrl + "/stub/v1/ping", String.class);
            sw.stop();
            deps.put("stub", (pong != null && !pong.isBlank()) ? "UP" : "DOWN");
            if (pong == null || pong.isBlank()) up = false;
            deps.put("stubLatencyMs", String.valueOf((long) sw.getTotalTimeMillis()));
        } catch (RestClientException e) {
            deps.put("stub", "DOWN");
            up = false;
        }

        try {
            PdfSmokeTest.run();
            deps.put("pdfEngine", "UP");
        } catch (Exception e) {
            deps.put("pdfEngine", "DOWN");
            up = false;
        }

        return new HealthResponse(up ? "UP" : "DOWN", version, buildTime, deps, null);
    }

    @Schema(name = "HealthResponse")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HealthResponse {
        @Schema(example = "UP")
        private String status;
        private String version;
        private String buildTime;
        private Map<String, String> dependencies;
        private Map<String, Object> extra;

        public HealthResponse() {}

        public HealthResponse(String status, String version, String buildTime,
                              Map<String, String> dependencies, Map<String, Object> extra) {
            this.status = status; this.version = version; this.buildTime = buildTime;
            this.dependencies = dependencies; this.extra = extra;
        }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getBuildTime() { return buildTime; }
        public void setBuildTime(String buildTime) { this.buildTime = buildTime; }
        public Map<String, String> getDependencies() { return dependencies; }
        public void setDependencies(Map<String, String> dependencies) { this.dependencies = dependencies; }
        public Map<String, Object> getExtra() { return extra; }
        public void setExtra(Map<String, Object> extra) { this.extra = extra; }
    }

    static class PdfSmokeTest {
        public static void run() throws Exception {
            try {
                Class<?> docClass = Class.forName("com.itextpdf.text.Document");
                Class<?> writerClass = Class.forName("com.itextpdf.text.pdf.PdfWriter");
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                Object doc = docClass.getConstructor().newInstance();
                java.lang.reflect.Method open = docClass.getMethod("open");
                java.lang.reflect.Method close = docClass.getMethod("close");
                java.lang.reflect.Method add = docClass.getMethod("add", Class.forName("com.itextpdf.text.Element"));

                java.lang.reflect.Method getInstance = writerClass.getMethod("getInstance",
                        docClass, java.io.OutputStream.class);
                getInstance.invoke(null, doc, baos);

                open.invoke(doc);
                Object paragraph = Class.forName("com.itextpdf.text.Paragraph")
                        .getConstructor(String.class).newInstance("health ok");
                add.invoke(doc, paragraph);
                close.invoke(doc);

            } catch (ClassNotFoundException e) {
                // opzionale: se iText non è presente, consideralo UP
            }
        }
    }
}
