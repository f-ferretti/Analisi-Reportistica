package it.unimol.report_management.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import it.unimol.report_management.dto.student.ExamResultDTO;
import it.unimol.report_management.dto.student.GraduationEstimateDTO;
import it.unimol.report_management.dto.student.PlanItemDTO;
import it.unimol.report_management.dto.student.StudentDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PdfUtil {

    /** PDF da singola immagine (compat) */
    public static byte[] imageToPdf(BufferedImage image, String titolo, Map<String, String> meta) {
        try {
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, baos);

            if (meta != null) {
                if (meta.containsKey("Title")) doc.addTitle(meta.get("Title"));
                if (meta.containsKey("Author")) doc.addAuthor(meta.get("Author"));
                if (meta.containsKey("Subject")) doc.addSubject(meta.get("Subject"));
                if (meta.containsKey("Keywords")) doc.addKeywords(meta.get("Keywords"));
                if (meta.containsKey("Creator")) doc.addCreator(meta.get("Creator"));
            }

            doc.open();
            if (titolo != null && !titolo.isBlank()) {
                Paragraph p = new Paragraph(titolo, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
                p.setSpacingAfter(10f);
                doc.add(p);
            }

            ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imgBytes);
            Image itImg = Image.getInstance(imgBytes.toByteArray());
            itImg.scaleToFit(PageSize.A4.getHeight() - 72, PageSize.A4.getWidth() - 72);
            doc.add(itImg);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Errore generazione PDF immagine: " + e.getMessage(), e);
        }
    }

    /** Report completo studente (senza lodi/punti carriera/tesi/in-tempo) - NULL-SAFE */
    public static byte[] studentSummary(StudentDTO student,
                                        List<ExamResultDTO> passed,
                                        List<PlanItemDTO> pending,
                                        GraduationEstimateDTO estimate,
                                        BufferedImage chart) {
        try {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // Header
            doc.add(new Paragraph("Report studente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            String nome = student != null && student.getNome() != null ? student.getNome() : "";
            String matr = student != null && student.getMatricola() != null ? student.getMatricola() : "";
            doc.add(new Paragraph((nome.isBlank() ? "Studente" : nome) + (matr.isBlank() ? "" : " - Matricola " + matr)));
            try {
                // coorte come int primitivo → niente null: stampo solo se > 0
                int coorte = student != null ? student.getCoorte() : 0;
                if (coorte > 0) doc.add(new Paragraph("Coorte: " + coorte));
            } catch (Throwable ignore) { /* compat con eventuale Integer */ }
            doc.add(Chunk.NEWLINE);

            // Esami superati
            int earned = 0;
            PdfPTable t1 = new PdfPTable(5);
            t1.setWidthPercentage(100);
            addHeader(t1, "Codice", "Corso", "CFU", "Voto", "Data");
            if (passed != null) {
                for (ExamResultDTO e : passed) {
                    if (e == null) continue;
                    String codice = safe(e.getCodiceCorso());
                    String nomeCorso = safe(e.getNomeCorso());
                    int cfu = safeInt(e.getCfu());
                    int voto = safeInt(e.getVoto());
                    boolean lode = safeBool(e.isLode());
                    LocalDate d = null;
                    try { d = e.getData(); } catch (Throwable ignore) {}

                    t1.addCell(codice);
                    t1.addCell(nomeCorso);
                    t1.addCell(String.valueOf(cfu));
                    t1.addCell(String.valueOf(voto) + (lode ? "L" : ""));
                    t1.addCell(d != null ? d.toString() : "");
                    earned += cfu;
                }
            }
            doc.add(new Paragraph("CFU conseguiti: " + earned + " / 180 (" + String.format("%.2f", (earned * 100.0 / 180)) + "%)"));
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("Esami superati", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            doc.add(t1);
            doc.add(Chunk.NEWLINE);

            // Esami mancanti
            PdfPTable t2 = new PdfPTable(4);
            t2.setWidthPercentage(100);
            addHeader(t2, "Codice", "Corso", "CFU", "Anno corso");
            if (pending != null) {
                for (PlanItemDTO p : pending) {
                    if (p == null) continue;
                    String codice = safe(p.getCodice());
                    String nomeCorso = safe(p.getNome());
                    int cfu = safeInt(p.getCfu());
                    Integer annoCorso = null;
                    try { annoCorso = p.getAnnoCorso(); } catch (Throwable ignore) {}

                    t2.addCell(codice);
                    t2.addCell(nomeCorso);
                    t2.addCell(String.valueOf(cfu));
                    t2.addCell(annoCorso != null ? String.valueOf(annoCorso) : "");
                }
            }
            doc.add(new Paragraph("Esami mancanti", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            doc.add(t2);
            doc.add(Chunk.NEWLINE);

            // Grafico progressione
            if (chart != null) {
                ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
                javax.imageio.ImageIO.write(chart, "png", imgBytes);
                Image itImg = Image.getInstance(imgBytes.toByteArray());
                itImg.scaleToFit(520, 300);
                doc.add(new Paragraph("Progressione voti", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                doc.add(itImg);
                doc.add(Chunk.NEWLINE);
            }

            // Stima laurea
            if (estimate != null) {
                PdfPTable t3 = new PdfPTable(3);
                t3.setWidthPercentage(100);
                addHeader(t3, "Media/30", "Base/110", "Finale");
                t3.addCell(String.valueOf(estimate.getWeightedAvg30()));
                t3.addCell(String.valueOf(estimate.getBase110()));
                t3.addCell(String.valueOf(estimate.getFinalEstimate()));
                doc.add(new Paragraph("Stima voto di laurea", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                doc.add(t3);
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Errore generazione PDF report studente: " + e.getMessage(), e);
        }
    }

    /* ===== Helpers ===== */
    private static void addHeader(PdfPTable t, String... labels) {
        for (String s : labels) {
            PdfPCell c = new PdfPCell(new Phrase(s, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
            c.setBackgroundColor(new BaseColor(240, 240, 240));
            t.addCell(c);
        }
    }
    private static String safe(String s) { return s == null ? "" : s; }
    private static int safeInt(int v) { return v; } // primitivi: già safe
    private static boolean safeBool(boolean b) { return b; } // primitivi: già safe
}