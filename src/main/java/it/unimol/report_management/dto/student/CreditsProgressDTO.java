package it.unimol.report_management.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Progresso CFU dello studente")
public class CreditsProgressDTO {
    private int earnedCfu;
    private int totalCfu;
    private int missingCfu;
    private double percent;

    public CreditsProgressDTO() {}
    public CreditsProgressDTO(int earnedCfu, int totalCfu) {
        this.earnedCfu = earnedCfu;
        this.totalCfu = totalCfu;
        this.missingCfu = Math.max(0, totalCfu - earnedCfu);
        this.percent = totalCfu > 0 ? (earnedCfu * 100.0 / totalCfu) : 0.0;
    }

    public int getEarnedCfu() { return earnedCfu; }
    public void setEarnedCfu(int earnedCfu) { this.earnedCfu = earnedCfu; }
    public int getTotalCfu() { return totalCfu; }
    public void setTotalCfu(int totalCfu) { this.totalCfu = totalCfu; }
    public int getMissingCfu() { return missingCfu; }
    public void setMissingCfu(int missingCfu) { this.missingCfu = missingCfu; }
    public double getPercent() { return percent; }
    public void setPercent(double percent) { this.percent = percent; }
}
