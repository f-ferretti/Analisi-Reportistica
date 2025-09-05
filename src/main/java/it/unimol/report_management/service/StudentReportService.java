package it.unimol.report_management.service;

import it.unimol.report_management.client.StubClient;
import it.unimol.report_management.dto.student.CreditsProgressDTO;
import it.unimol.report_management.dto.student.ExamResultDTO;
import it.unimol.report_management.dto.student.GraduationEstimateDTO;
import it.unimol.report_management.dto.student.PlanItemDTO;
import it.unimol.report_management.dto.student.StudentDTO;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class StudentReportService {

    private final StubClient stub;
    private final CacheService cache;

    public StudentReportService(StubClient stub, CacheService cache) {
        this.stub = stub;
        this.cache = cache;
    }

    /* ========================= ENDPOINT LOGIC ========================= */

    /** Esami mancanti = piano - esami superati (con voto >= 18). */
    public List<PlanItemDTO> pendingExams(String matricola) {
        validateMatricola(matricola);

        List<PlanItemDTO> piano = stub.getStudentPlan(matricola);
        if (piano == null) {
            throw new ResponseStatusException(NOT_FOUND,
                    "Piano di studi non trovato per lo studente " + matricola);
        }

        // Codici degli esami superati
        List<ExamResultDTO> exams = stub.getStudentExams(matricola);
        Set<String> codiciSuperati = new HashSet<>();
        if (exams != null) {
            for (ExamResultDTO e : exams) {
                if (e != null && e.getVoto() >= 18) {
                    String code = e.getCodiceCorso();
                    if (code != null && !code.isBlank()) codiciSuperati.add(code);
                }
            }
        }

        return piano.stream()
                .filter(Objects::nonNull)
                .filter(pi -> {
                    String code = pi.getCodice();
                    return code == null || !codiciSuperati.contains(code);
                })
                .sorted(Comparator
                        .comparing((PlanItemDTO pi) -> Optional.ofNullable(pi.getAnnoCorso()).orElse(99))
                        .thenComparing(pi -> Optional.ofNullable(pi.getCodice()).orElse("")))
                .collect(Collectors.toList());
    }

    /** Esami superati (ordinati per data poi codice). */
    public List<ExamResultDTO> passedExams(String matricola) {
        validateMatricola(matricola);
        List<ExamResultDTO> exams = stub.getStudentExams(matricola);
        if (exams == null) return List.of();

        return exams.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing((ExamResultDTO e) -> Optional.ofNullable(e.getData()).orElse(LocalDate.MIN))
                        .thenComparing(e -> Optional.ofNullable(e.getCodiceCorso()).orElse("")))
                .collect(Collectors.toList());
    }

    /** Progresso CFU: totali dal piano, conseguiti dagli esami con voto >= 18. */
    public CreditsProgressDTO creditsProgress(String matricola) {
        validateMatricola(matricola);

        List<PlanItemDTO> piano = stub.getStudentPlan(matricola);
        if (piano == null) {
            throw new ResponseStatusException(NOT_FOUND,
                    "Piano di studi non trovato per lo studente " + matricola);
        }

        int totalCfu = piano.stream()
                .filter(Objects::nonNull)
                .mapToInt(PlanItemDTO::getCfu)
                .sum();

        List<ExamResultDTO> exams = stub.getStudentExams(matricola);
        int earnedCfu = 0;
        if (exams != null) {
            for (ExamResultDTO e : exams) {
                if (e != null && e.getVoto() >= 18) {
                    earnedCfu += Math.max(0, e.getCfu());
                }
            }
        }

        // NB: il tuo DTO ha il costruttore (earnedCfu, totalCfu)
        return new CreditsProgressDTO(earnedCfu, totalCfu);
    }

    /**
     * Stima voto di laurea:
     *  - media pesata su 30 (solo voti, niente lodi)
     *  - base/110 = conversione della media
     *  - finale/110 = base + bonus forniti dallo stub (tesi/carriera).
     */
    public GraduationEstimateDTO graduationEstimateFromStub(String matricola, Integer annoAccademico) {
        validateMatricola(matricola);

        List<ExamResultDTO> exams = stub.getStudentExams(matricola);
        if (exams == null || exams.isEmpty()) {
            throw new ResponseStatusException(NOT_FOUND,
                    "Esami dello studente " + matricola + " non trovati");
        }

        int sumCfu = 0;
        int sumVotoXcfu = 0;
        for (ExamResultDTO e : exams) {
            if (e == null) continue;
            int cfu = Math.max(0, e.getCfu());
            int voto = e.getVoto();
            if (cfu > 0 && voto >= 18) {
                sumCfu += cfu;
                sumVotoXcfu += voto * cfu;
            }
        }
        if (sumCfu == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Formato dati stub non valido: nessun CFU utile per media");
        }

        double weighted = sumVotoXcfu / (double) sumCfu;          // media in /30
        int base110 = (int) Math.round(weighted * 110.0 / 30.0);  // base in /110

        int aa = (annoAccademico != null) ? annoAccademico : Year.now().getValue();
        Map<String, Object> fattori = stub.getGraduationFactors(matricola, aa);
        int bonus = 0;
        if (fattori != null) {
            Object pt = fattori.get("puntiTesi");
            Object pc = fattori.get("puntiCarriera");
            if (pt instanceof Number) bonus += ((Number) pt).intValue();
            if (pc instanceof Number) bonus += ((Number) pc).intValue();
        }
        int finalEst = Math.min(110, base110 + bonus);

        GraduationEstimateDTO dto = new GraduationEstimateDTO();
        dto.setWeightedAvg30(Math.round(weighted * 100.0) / 100.0);
        dto.setBase110(base110);
        dto.setFinalEstimate(finalEst);
        return dto;
    }

    /** PDF riassuntivo (download diretto). */
    public byte[] summaryPdf(String matricola, Integer annoAccademico) {
        validateMatricola(matricola);

        // Caching DB (TTL configurabile via cache.ttl-minutes)
        Map<String,Object> params = new LinkedHashMap<>();
        if (annoAccademico != null) params.put("annoAccademico", annoAccademico);
        Optional<byte[]> cached = cache.get("STUDENT_SUMMARY", matricola, params, "pdf");
        if (cached.isPresent()) return cached.get();

        List<ExamResultDTO> passed = passedExams(matricola);
        List<PlanItemDTO> pending = pendingExams(matricola);
        GraduationEstimateDTO estimate = graduationEstimateFromStub(matricola, annoAccademico);

        // Costruisco lo StudentDTO (serve al PdfUtil). Imposto almeno la matricola.
        StudentDTO student = buildStudentDTO(matricola);

        // Creo il grafico dell'andamento dei voti
        BufferedImage chart = createGradesChart(passed, "Andamento voti - " + matricola);

        byte[] pdfBytes = PdfUtil.studentSummary(student, passed, pending, estimate, chart);
        cache.put("STUDENT_SUMMARY", matricola, params, "pdf", pdfBytes);
        return pdfBytes;
    }

    private BufferedImage createGradesChart(List<ExamResultDTO> exams, String title) {
        if (exams == null || exams.isEmpty()) return null;

        // Ordino gli esami per data
        List<ExamResultDTO> sortedExams = exams.stream()
                .filter(e -> e != null && e.getData() != null && e.getVoto() >= 18)
                .sorted(Comparator.comparing(ExamResultDTO::getData))
                .collect(Collectors.toList());

        if (sortedExams.isEmpty()) return null;

        int width = 900, height = 400;
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

            // Griglia e label asse Y (voti da 18 a 30)
            g2.setFont(g2.getFont().deriveFont(11f));
            for (int voto = 18; voto <= 30; voto += 2) {
                int y = y0 - (int)((voto - 18) * plotH / 12.0);
                g2.setColor(new Color(230, 230, 230));
                g2.drawLine(x0, y, x0 + plotW, y);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(String.valueOf(voto), x0 - 25, y + 4);
            }

            // Punti e linee del grafico
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(100, 149, 237)); // CornflowerBlue

            int numExams = sortedExams.size();
            int[] xPoints = new int[numExams];
            int[] yPoints = new int[numExams];

            for (int i = 0; i < numExams; i++) {
                ExamResultDTO exam = sortedExams.get(i);
                xPoints[i] = x0 + (i * plotW) / (numExams - 1);
                yPoints[i] = y0 - (int)((exam.getVoto() - 18) * plotH / 12.0);

                // Punto
                g2.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);

                // Linea al punto precedente
                if (i > 0) {
                    g2.drawLine(xPoints[i-1], yPoints[i-1], xPoints[i], yPoints[i]);
                }

                // Label corso ruotata
                g2.setColor(Color.BLACK);
                g2.rotate(-Math.PI/4);
                int tx = (int)(xPoints[i] * Math.cos(Math.PI/4) - (y0 + 20) * Math.sin(Math.PI/4));
                int ty = (int)(xPoints[i] * Math.sin(Math.PI/4) + (y0 + 20) * Math.cos(Math.PI/4));
                g2.drawString(exam.getCodiceCorso(), tx, ty);
                g2.rotate(Math.PI/4);
                g2.setColor(new Color(100, 149, 237));
            }

            // Etichette assi
            g2.setColor(Color.BLACK);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
            g2.drawString("Esami in ordine cronologico", x0 + plotW/2 - 70, height - 15);
            g2.rotate(-Math.PI/2);
            g2.drawString("Voto", -(top + plotH/2 + 20), 20);
            g2.rotate(Math.PI/2);

            // Info totale
            g2.setFont(g2.getFont().deriveFont(10f));
            g2.setColor(Color.GRAY);
            g2.drawString("N = " + numExams, width - right - 60, height - 10);

        } finally {
            g2.dispose();
        }
        return img;
    }

    /* ========================= HELPERS ========================= */

    private void validateMatricola(String matricola) {
        if (matricola == null || !matricola.matches("^\\d{6}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matricola non valida (6 cifre)");
        }
    }

    /**
     * Crea uno StudentDTO impostando almeno la matricola.
     * Tenta nell’ordine:
     *  1) costruttore StudentDTO(String matricola)
     *  2) costruttore vuoto + setMatricola(String)
     *  3) costruttore vuoto + scrittura diretta del campo 'matricola'
     */
    private StudentDTO buildStudentDTO(String matricola) {
        try {
            // 1) ctor(String)
            try {
                Constructor<StudentDTO> c1 = StudentDTO.class.getDeclaredConstructor(String.class);
                c1.setAccessible(true);
                return c1.newInstance(matricola);
            } catch (NoSuchMethodException ignored) { }

            // 2) no-args + setter
            try {
                StudentDTO s = StudentDTO.class.getDeclaredConstructor().newInstance();
                Method m = StudentDTO.class.getMethod("setMatricola", String.class);
                m.invoke(s, matricola);
                return s;
            } catch (NoSuchMethodException ignored) { }

            // 3) no-args + campo
            StudentDTO s = StudentDTO.class.getDeclaredConstructor().newInstance();
            Field f = null;
            for (Field fld : StudentDTO.class.getDeclaredFields()) {
                if ("matricola".equalsIgnoreCase(fld.getName())) { f = fld; break; }
            }
            if (f != null) {
                f.setAccessible(true);
                f.set(s, matricola);
                return s;
            }

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Impossibile valorizzare StudentDTO.matricola");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Errore creazione StudentDTO", e);
        }
    }
}