package it.unimol.report_management.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Stima voto di laurea (senza dettagli su lodi/tesi/carreira/in-tempo)")
public class GraduationEstimateDTO {
    @Schema(description = "Media ponderata in trentesimi.")
    private double weightedAvg30;

    @Schema(description = "Base di laurea in centodecimi calcolata dalla media (senza bonus).")
    private int base110;

    @Schema(description = "Stima finale in centodecimi (bonus già applicati lato stub).")
    private int finalEstimate;

    public GraduationEstimateDTO() {}

    public double getWeightedAvg30() { return weightedAvg30; }
    public void setWeightedAvg30(double weightedAvg30) { this.weightedAvg30 = weightedAvg30; }

    public int getBase110() { return base110; }
    public void setBase110(int base110) { this.base110 = base110; }

    public int getFinalEstimate() { return finalEstimate; }
    public void setFinalEstimate(int finalEstimate) { this.finalEstimate = finalEstimate; }
}