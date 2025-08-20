package it.unimol.report_management.util;

import it.unimol.report_management.dto.student.ExamResultDTO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class ChartUtil {

    public static BufferedImage gradeDistribution(Map<Integer, Integer> hist, int width, int height, String title) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString(title, 20, 30);

        int left = 60, right = 20, top = 50, bottom = 40;
        int w = width - left - right;
        int h = height - top - bottom;

        int maxCount = 1;
        for (Integer c : hist.values()) if (c != null && c > maxCount) maxCount = c;

        int bins = hist.size();
        int barGap = 6;
        int barWidth = Math.max(1, (w - (bins + 1) * barGap) / Math.max(1, bins));
        int x = left + barGap;

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawLine(left, top, left, top + h);
        g.drawLine(left, top + h, left + w, top + h);

        for (Map.Entry<Integer, Integer> e : hist.entrySet()) {
            int count = e.getValue() == null ? 0 : e.getValue();
            int barHeight = (int) ((count / (double) maxCount) * (h - 10));
            int y = top + h - barHeight;
            g.setColor(new Color(100, 100, 200));
            g.fillRect(x, y, barWidth, barHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, barWidth, barHeight);
            String lab = String.valueOf(e.getKey());
            int tw = g.getFontMetrics().stringWidth(lab);
            g.drawString(lab, x + (barWidth - tw) / 2, top + h + 15);
            if (count > 0) {
                String val = String.valueOf(count);
                int tv = g.getFontMetrics().stringWidth(val);
                g.drawString(val, x + (barWidth - tv) / 2, y - 4);
            }
            x += barWidth + barGap;
        }
        g.dispose();
        return img;
    }

    public static BufferedImage gradesProgression(List<ExamResultDTO> exams, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Progressione voti", 20, 30);

        int left = 60, right = 20, top = 50, bottom = 40;
        int w = width - left - right;
        int h = height - top - bottom;

        g.setColor(Color.BLACK);
        g.drawLine(left, top, left, top + h);
        g.drawLine(left, top + h, left + w, top + h);

        if (exams == null || exams.isEmpty()) {
            g.drawString("Nessun esame", left + 10, top + 20);
            g.dispose();
            return img;
        }

        int n = exams.size();
        int minGrade = 18, maxGrade = 30;
        double stepX = n > 1 ? (w * 1.0 / (n - 1)) : 0;

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (int gr = minGrade; gr <= maxGrade; gr += 2) {
            int y = top + (int) ((maxGrade - gr) * (h * 1.0 / (maxGrade - minGrade)));
            g.setColor(new Color(230,230,230));
            g.drawLine(left, y, left + w, y);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(gr), left - 30, y + 4);
        }

        g.setStroke(new BasicStroke(2f));
        int prevX = -1, prevY = -1;
        for (int i = 0; i < n; i++) {
            Integer grade = exams.get(i) != null ? exams.get(i).getVoto() : null;
            if (grade == null) continue;
            grade = Math.max(minGrade, Math.min(maxGrade, grade));
            int x = left + (int) Math.round(i * stepX);
            int y = top + (int) ((maxGrade - grade) * (h * 1.0 / (maxGrade - minGrade)));

            g.setColor(new Color(50,120,200));
            g.fillOval(x - 3, y - 3, 6, 6);
            if (prevX >= 0) g.drawLine(prevX, prevY, x, y);
            prevX = x; prevY = y;
        }

        g.dispose();
        return img;
    }
}
