// src/main/java/it/unimol/report_management/pdf/PdfGenerator.java
package it.unimol.report_management.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generatore PDF per i report, con logo nel header,
 * titolo centrato, tabella e footer con numero di pagina.
 */
public class PdfGenerator {
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8);

    // Mappa chiave->etichetta italiana
    private static final Map<String, String> ETICHETTE = new LinkedHashMap<>();
    static {
        ETICHETTE.put("feedback", "Feedback");
        ETICHETTE.put("ratings", "Valutazioni");
        ETICHETTE.put("average", "Media");
        ETICHETTE.put("progress", "Progresso");
        ETICHETTE.put("completion_rate", "Tasso completamento");
        ETICHETTE.put("grades", "Voti");
        ETICHETTE.put("distribution", "Distribuzione voti");
        ETICHETTE.put("performance", "Performance");
        ETICHETTE.put("activity", "Attività");
        ETICHETTE.put("average_teacher_rating", "Media valutazioni docenti");
        ETICHETTE.put("average_student_grade", "Media voti studenti");
        ETICHETTE.put("average_course_completion", "Tasso completamento corsi");
        ETICHETTE.put("generated_at", "Data generazione");
    }

    /**
     * Genera il PDF.
     * @param titolo titolo del report
     * @param righe elenco di righe (map chiave->valore)
     * @return array di byte del PDF
     */
    public static byte[] createTablePdf(String titolo, List<Map<String, Object>> righe) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            HeaderFooterPageEvent event = new HeaderFooterPageEvent(titolo);
            writer.setPageEvent(event);
            document.open();

            if (righe == null || righe.isEmpty()) {
                Paragraph vuoto = new Paragraph("Nessun dato disponibile.", CELL_FONT);
                vuoto.setAlignment(Element.ALIGN_CENTER);
                document.add(vuoto);
            } else {
                // Controlla prima riga
                Map<String, Object> primaRiga = righe.get(0);
                if (primaRiga == null || primaRiga.isEmpty()) {
                    Paragraph vuoto = new Paragraph("Nessun dato disponibile.", CELL_FONT);
                    vuoto.setAlignment(Element.ALIGN_CENTER);
                    document.add(vuoto);
                } else {
                    String[] colonne = primaRiga.keySet().toArray(new String[0]);
                    PdfPTable table = new PdfPTable(colonne.length);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);

                    // Intestazioni
                    for (String key : colonne) {
                        String label = ETICHETTE.getOrDefault(key, toItalianLabel(key));
                        PdfPCell h = new PdfPCell(new Phrase(label, HEADER_FONT));
                        h.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        h.setHorizontalAlignment(Element.ALIGN_CENTER);
                        h.setPadding(5);
                        table.addCell(h);
                    }
                    // Dati
                    for (Map<String, Object> row : righe) {
                        for (String key : colonne) {
                            String testo = String.valueOf(row.getOrDefault(key, ""));
                            PdfPCell c = new PdfPCell(new Phrase(testo, CELL_FONT));
                            c.setPadding(5);
                            table.addCell(c);
                        }
                    }
                    document.add(table);
                }
            }
            document.close();
        } catch (Exception e) {
            System.err.println("Errore generazione PDF: " + e.getMessage());
            throw new RuntimeException("Errore generazione PDF: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }

    private static String toItalianLabel(String key) {
        String base = key.replace('_', ' ').toLowerCase(Locale.ITALIAN);
        return Character.toUpperCase(base.charAt(0)) + base.substring(1);
    }

    private static class HeaderFooterPageEvent extends PdfPageEventHelper {
        private final String titolo;
        private Image logo;

        public HeaderFooterPageEvent(String titolo) {
            this.titolo = titolo;
            try {
                URL url = getClass().getClassLoader().getResource("logo.png");
                if (url != null) {
                    logo = Image.getInstance(url);
                    logo.scaleToFit(80, 40);
                }
            } catch (Exception ex) {
                logo = null;
            }
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            Rectangle pageSize = document.getPageSize();
            try {
                if (logo != null) {
                    float x = 36f; // margine sinistro fisso di 36pt
                    float y = pageSize.getTop() - logo.getScaledHeight() - 10;
                    logo.setAbsolutePosition(x, y);
                    writer.getDirectContentUnder().addImage(logo);
                }
                ColumnText.showTextAligned(
                        writer.getDirectContent(), Element.ALIGN_CENTER,
                        new Phrase(titolo, TITLE_FONT),
                        pageSize.getWidth() / 2,
                        pageSize.getTop() - 34f, // posiziona titolo sotto margine superiore di 54pt
                        0
                );
            } catch (Exception ignore) {}
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle pageSize = document.getPageSize();
            PdfPTable footer = new PdfPTable(1);
            try {
                // Calcola larghezza footer rispetto ai margini hardcoded (36pt)
                footer.setTotalWidth(pageSize.getWidth() - 72); // 36pt margine sx + 36pt dx
                footer.getDefaultCell().setBorder(Rectangle.TOP);
                footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                footer.addCell(new Phrase(
                        String.format("Pagina %d", writer.getPageNumber()), FOOTER_FONT
                ));
                footer.writeSelectedRows(0, -1,
                        36, // X = margine sinistro fisso
                        26, // Y = margine inferiore 36pt - 10pt
                        writer.getDirectContent());
            } catch (Exception ex) {
                throw new ExceptionConverter(ex);
            }
        }
    }
}