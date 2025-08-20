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

import java.awt.image.BufferedImage;
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

    public StudentReportService(StubClient stub) {
        this.stub = stub;
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

        List<ExamResultDTO> passed  = passedExams(matricola);
        List<PlanItemDTO>   pending = pendingExams(matricola);
        GraduationEstimateDTO estimate = graduationEstimateFromStub(matricola, annoAccademico);

        // Costruisco lo StudentDTO (serve al PdfUtil). Imposto almeno la matricola.
        StudentDTO student = buildStudentDTO(matricola);

        // L’ultimo parametro è un grafico/immagine opzionale: qui non lo usiamo.
        BufferedImage chart = null;

        return PdfUtil.studentSummary(student, passed, pending, estimate, chart);
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