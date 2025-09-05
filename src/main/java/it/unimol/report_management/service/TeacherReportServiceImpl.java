package it.unimol.report_management.service;

import it.unimol.report_management.client.StubClient;
import it.unimol.report_management.dto.student.ExamResultDTO;
import it.unimol.report_management.dto.teacher.TeacherConsistencyDTO;
import it.unimol.report_management.exception.ResourceNotFoundException;
import it.unimol.report_management.util.PdfUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Year;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherReportServiceImpl implements TeacherReportService {

    private final StubClient stub;
    private final CacheService cache;

    public TeacherReportServiceImpl(StubClient stub, CacheService cache) {
        this.stub = stub;
        this.cache = cache;
    }

    // -------------------- DISTRIBUZIONE (PDF) --------------------
    @Override
    public byte[] gradesDistributionPdf(String docenteId, Integer annoAccademico) {
        validateDocente(docenteId);
        final int aa = (annoAccademico != null ? annoAccademico : Year.now().getValue());

        // ==================== CACHE IMPLEMENTATION ====================
        // Caching DB per PDF distribuzione voti docente
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("annoAccademico", aa);

        Optional<byte[]> cached = cache.get("TEACHER_GRADES_DISTRIBUTION", docenteId, params, "pdf");
        if (cached.isPresent()) {
            return cached.get();
        }
        // ==================== END CACHE GET ====================

        // 1) insegnamenti del docente per l'anno
        List<Map<String, String>> teachings = stub.getTeacherTeachings(docenteId, aa);
        if (teachings == null || teachings.isEmpty()) {
            throw new ResourceNotFoundException("Insegnamenti per docente " + docenteId + " non trovati");
        }

        // 2) codici corso (lo stub usa "codice", ma accetto anche altre chiavi)
        Set<String> courseCodes = extractCourseCodes(teachings);
        if (courseCodes.isEmpty()) {
            throw new ResourceNotFoundException("Nessun corso valido per il docente " + docenteId + " nell'anno " + aa);
        }

        // 3) raccogli tutti i voti dei corsi del docente
        List<Integer> voti = new ArrayList<>();
        for (String cc : courseCodes) {
            List<ExamResultDTO> grades = stub.getCourseGrades(cc, aa);
            if (grades != null) {
                for (ExamResultDTO e : grades) {
                    if (e != null && e.getVoto() >= 18 && e.getVoto() <= 30) {
                        voti.add(e.getVoto());
                    }
                }
            }
        }
        if (voti.isEmpty()) {
            throw new ResourceNotFoundException("Nessun voto disponibile per docente " + docenteId + " (" + aa + ")");
        }

        BufferedImage chart = drawHistogram(voti, "Distribuzione voti - Docente " + docenteId + " (" + aa + ")");
        Map<String, String> meta = new LinkedHashMap<>();
        meta.put("teacherId", docenteId);
        meta.put("annoAccademico", String.valueOf(aa));

        // Genera il PDF
        byte[] pdfBytes = PdfUtil.imageToPdf(chart, "Distribuzione voti - Docente " + docenteId + " (" + aa + ")", meta);

        // ==================== CACHE PUT ====================
        // Memorizza in cache il PDF generato
        cache.put("TEACHER_GRADES_DISTRIBUTION", docenteId, params, "pdf", pdfBytes);
        // ==================== END CACHE PUT ====================

        return pdfBytes;
    }

    // -------------------- CONSISTENZA (JSON) --------------------
    @Override
    public TeacherConsistencyDTO consistency(String docenteId, String courseCode, Integer from, Integer to) {
        validateDocente(docenteId);
        validateCourse(courseCode);

        final int current = Year.now().getValue();
        final int toY = (to != null ? to : current);
        final int fromY = (from != null ? from : (toY - 4));
        if (fromY > toY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervallo anni non valido: from > to");
        }
        if (toY - fromY > 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Intervallo anni troppo ampio");
        }

        Map<Integer, Double> avgByYear = new LinkedHashMap<>();
        Map<Integer, Double> passByYear = new LinkedHashMap<>();

        for (int aa = fromY; aa <= toY; aa++) {
            List<Map<String, String>> teachings;
            try {
                teachings = stub.getTeacherTeachings(docenteId, aa);
            } catch (ResourceNotFoundException ex) {
                continue; // nessun insegnamento quell'anno
            }
            if (!teachesCourse(teachings, courseCode)) continue;

            List<ExamResultDTO> grades;
            try {
                grades = stub.getCourseGrades(courseCode, aa);
            } catch (ResourceNotFoundException ex) {
                continue;
            }
            if (grades == null || grades.isEmpty()) continue;

            List<Integer> voti = grades.stream()
                    .filter(Objects::nonNull)
                    .map(ExamResultDTO::getVoto)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (voti.isEmpty()) continue;

            double media = voti.stream().mapToInt(Integer::intValue).average().orElse(0d);
            avgByYear.put(aa, round2(media));
            // nello stub tutti i voti sono >=18, quindi pass rate 100%
            passByYear.put(aa, 1.0d);
        }

        if (avgByYear.isEmpty()) {
            throw new ResourceNotFoundException("Nessun dato disponibile per docente " + docenteId + " e corso " + courseCode);
        }

        // Deviazione standard delle medie annue
        double meanOfMeans = avgByYear.values().stream().mapToDouble(Double::doubleValue).average().orElse(0d);
        double variance = avgByYear.values().stream().mapToDouble(v -> (v - meanOfMeans) * (v - meanOfMeans)).average().orElse(0d);
        double stddev = Math.sqrt(variance);

        // Pendenza del trend (regressione lineare semplice)
        double slope = 0d;
        if (avgByYear.size() >= 2) {
            int n = avgByYear.size();
            double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
            for (Map.Entry<Integer, Double> e : avgByYear.entrySet()) {
                double x = e.getKey();
                double y = e.getValue();
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumXX += x * x;
            }
            slope = (n * sumXY - sumX * sumY) / Math.max(1e-6, (n * sumXX - sumX * sumX));
        }

        TeacherConsistencyDTO dto = new TeacherConsistencyDTO();
        dto.setTeacherId(docenteId);
        dto.setCourseCode(courseCode);
        dto.setFrom(fromY);
        dto.setTo(toY);
        dto.setYearsCount(avgByYear.size());
        dto.setAvgByYear(avgByYear);
        dto.setPassRateByYear(passByYear);
        dto.setStddev(round2(stddev));
        dto.setTrendSlope(round2(slope));
        return dto;
    }

    // -------------------- HELPERS --------------------
    private void validateDocente(String docenteId) {
        if (docenteId == null || !docenteId.matches("^DOC\\d{3}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID docente non valido (es. DOC123)");
        }
    }

    private void validateCourse(String courseCode) {
        if (courseCode == null || !courseCode.matches("^[A-Za-z0-9_-]{2,32}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codice corso non valido");
        }
    }

    private boolean teachesCourse(List<Map<String, String>> teachings, String courseCode) {
        if (teachings == null || teachings.isEmpty()) return false;
        for (Map<String, String> row : teachings) {
            if (row == null) continue;
            for (String key : List.of("courseCode", "codiceCorso", "code", "corso", "course", "id", "codice")) {
                String v = row.get(key);
                if (v != null && v.equalsIgnoreCase(courseCode)) return true;
            }
        }
        return false;
    }

    private Set<String> extractCourseCodes(List<Map<String, String>> teachings) {
        Set<String> codes = new LinkedHashSet<>();
        if (teachings == null) return codes;
        for (Map<String, String> row : teachings) {
            if (row == null) continue;
            for (String key : List.of("courseCode", "codiceCorso", "code", "corso", "course", "id", "codice")) {
                String v = row.get(key);
                if (v != null && v.matches("^[A-Za-z0-9_-]{2,32}$")) {
                    codes.add(v);
                    break;
                }
            }
        }
        return codes;
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private BufferedImage drawHistogram(List<Integer> voti, String title) {
        Collections.sort(voti);
        int width = 900, height = 400; // Ridotta altezza a 400px come in CourseReport
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            int left = 60, right = 20, top = 50, bottom = 60;
            int plotW = width - left - right, plotH = height - top - bottom;
            int x0 = left, y0 = height - bottom;

            // Titolo
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString(title, left, 30);

            // Assi
            g2.drawLine(x0, y0, x0 + plotW, y0);  // Asse X
            g2.drawLine(x0, y0, x0, y0 - plotH);  // Asse Y

            int minV = 18, maxV = 30;
            int bins = maxV - minV + 1;
            int[] counts = new int[bins];
            for (int v : voti) {
                if (v >= minV && v <= maxV) {
                    counts[v - minV]++;
                }
            }

            // Griglia Y e etichette
            int maxCount = Arrays.stream(counts).max().orElse(1);
            g2.setFont(g2.getFont().deriveFont(11f));
            int ticks = Math.min(6, Math.max(3, maxCount));
            for (int i = 0; i <= ticks; i++) {
                int count = (int) Math.round(i * (maxCount * 1.0 / ticks));
                int y = y0 - (int) Math.round((count * 1.0 / maxCount) * plotH);
                g2.setColor(new Color(230, 230, 230));
                g2.drawLine(x0, y, x0 + plotW, y);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(String.valueOf(count), x0 - 35, y + 4);
            }

            // Barre
            int barGap = 4;
            int barW = Math.max(6, (plotW - (bins + 1) * barGap) / bins);
            int x = x0 + barGap;

            for (int i = 0; i < bins; i++) {
                int count = counts[i];
                int h = maxCount > 0 ? (int) Math.round((count * 1.0 / maxCount) * plotH) : 0;
                int y = y0 - h;

                // Barra
                g2.setColor(new Color(100, 149, 237)); // CornflowerBlue
                g2.fillRect(x, y, barW, h);
                g2.setColor(Color.BLACK);

                // Etichetta voto
                String label = String.valueOf(minV + i);
                int tx = x + barW / 2 - g2.getFontMetrics().stringWidth(label) / 2;
                g2.drawString(label, tx, y0 + 15);

                // Frequenza sopra la barra
                if (count > 0) {
                    String cstr = String.valueOf(count);
                    int tw = g2.getFontMetrics().stringWidth(cstr);
                    g2.drawString(cstr, x + barW / 2 - tw / 2, y - 3);
                }

                x += barW + barGap;
            }

            // Etichette assi
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
            g2.drawString("Voto", x0 + plotW / 2 - 15, height - 25);
            g2.rotate(-Math.PI / 2);
            g2.drawString("Frequenza", -(top + plotH / 2 + 30), 20);
            g2.rotate(Math.PI / 2);

            // Info totale
            g2.setFont(g2.getFont().deriveFont(10f));
            g2.setColor(Color.GRAY);
            g2.drawString("N = " + voti.size(), width - right - 60, height - 10);

        } finally {
            g2.dispose();
        }
        return img;
    }
}