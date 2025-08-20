package it.unimol.report_management.service;

import it.unimol.report_management.client.StubClient;
import it.unimol.report_management.dto.student.ExamResultDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

@Service
public class CourseReportServiceImpl implements CourseReportService {

    private final StubClient stub;

    public CourseReportServiceImpl(StubClient stub) {
        this.stub = stub;
    }

    @Override
    public List<Map<String, Integer>> enrollmentsByYear(String courseCode) {
        List<Map<String, Integer>> data = stub.getCourseEnrollmentsByYear(courseCode);

        if (data == null) {
            return List.of();
        }
        // normalizzo eventuali chiavi e valori null
        List<Map<String, Integer>> norm = new java.util.ArrayList<>();
        for (Map<String, Integer> r : data) {
            if (r == null) continue;
            Integer anno = r.get("anno");
            Integer iscritti = r.get("iscritti");
            if (anno == null) continue;
            norm.add(Map.of("anno", anno, "iscritti", iscritti == null ? 0 : Math.max(0, iscritti)));
        }
        return norm;
    }

    private Integer firstNonNull(Integer... vals) {
        for (Integer v : vals) if (v != null) return v;
        return null;
    }

    @Override
    public List<ExamResultDTO> courseGrades(String courseCode, Integer annoAccademico) {
        if (annoAccademico == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parametro annoAccademico obbligatorio");
        }
        List<ExamResultDTO> grades;
        try {
            grades = stub.getCourseGrades(courseCode, annoAccademico);
        } catch (Throwable t) {
            grades = stub.getCourseGradesToExamDTO(courseCode, annoAccademico);
        }
        if (grades == null || grades.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Voti non trovati per corso " + courseCode + " (" + annoAccademico + ")");
        }
        for (ExamResultDTO g : grades) {
            if (g == null || g.getVoto() < 18 || g.getVoto() > 30) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "Formato dati stub non valido per i voti del corso " + courseCode + " (" + annoAccademico + ")");
            }
        }
        return grades;
    }

    @Override
    public BufferedImage courseGradesDistributionChart(String courseCode, Integer annoAccademico) {
        List<ExamResultDTO> grades = courseGrades(courseCode, annoAccademico);

        int minV = 18, maxV = 30;
        int[] counts = new int[maxV - minV + 1];
        for (ExamResultDTO g : grades) {
            int v = g.getVoto();
            if (v >= minV && v <= maxV) counts[v - minV]++;
        }
        int maxCount = Arrays.stream(counts).max().orElse(1);

        int width = 900, height = 400;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE); g2.fillRect(0,0,width,height);

            int left=60,right=20,top=50,bottom=60;
            g2.setColor(Color.BLACK);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD,16f));
            g2.drawString("Distribuzione voti - " + courseCode + " (" + annoAccademico + ")", left, 30);

            int plotW = width-left-right, plotH = height-top-bottom, x0=left, y0=height-bottom;
            g2.drawLine(x0,y0,x0+plotW,y0); g2.drawLine(x0,y0,x0,y0-plotH);

            g2.setFont(g2.getFont().deriveFont(11f));
            int ticks = Math.min(6, Math.max(3, maxCount));
            for (int i=0;i<=ticks;i++){
                int c=(int)Math.round(i*(maxCount*1.0/ticks));
                int y=y0-(int)Math.round((c*1.0/maxCount)*plotH);
                g2.setColor(new Color(230,230,230)); g2.drawLine(x0,y,x0+plotW,y);
                g2.setColor(Color.DARK_GRAY); g2.drawString(String.valueOf(c), x0-35, y+4);
            }

            int bins = counts.length, barGap=4, barW=Math.max(6,(plotW-(bins+1)*barGap)/bins), x=x0+barGap;
            for (int i=0;i<bins;i++){
                int c=counts[i], h=maxCount>0?(int)Math.round((c*1.0/maxCount)*plotH):0, y=y0-h;
                g2.setColor(new Color(100,149,237)); g2.fillRect(x,y,barW,h);
                g2.setColor(Color.BLACK);
                String label=String.valueOf(minV+i);
                int tx=x+barW/2-g2.getFontMetrics().stringWidth(label)/2;
                g2.drawString(label, tx, y0+15);
                if (c>0){
                    String cstr=String.valueOf(c);
                    int tw=g2.getFontMetrics().stringWidth(cstr);
                    g2.drawString(cstr, x+barW/2 - tw/2, y-3);
                }
                x += barW + barGap;
            }
            g2.setFont(g2.getFont().deriveFont(Font.BOLD,12f));
            g2.drawString("Voto", x0+plotW/2-15, height-25);
            g2.rotate(-Math.PI/2); g2.drawString("Frequenza", -(top+plotH/2+30), 20); g2.rotate(Math.PI/2);
            g2.setFont(g2.getFont().deriveFont(10f)); g2.setColor(Color.GRAY);
            g2.drawString("N = "+grades.size(), width-right-60, height-10);
        } finally {
            g2.dispose();
        }
        return img;
    }
}