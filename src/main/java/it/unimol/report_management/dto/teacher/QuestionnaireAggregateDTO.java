package it.unimol.report_management.dto.teacher;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Aggregato questionari didattica")
public class QuestionnaireAggregateDTO {
    private String docenteId;
    private Integer aa;
    private int rispondenti;
    private Map<String, Double> indicatori;

    public String getDocenteId() { return docenteId; }
    public void setDocenteId(String docenteId) { this.docenteId = docenteId; }
    public Integer getAa() { return aa; }
    public void setAa(Integer aa) { this.aa = aa; }
    public int getRispondenti() { return rispondenti; }
    public void setRispondenti(int rispondenti) { this.rispondenti = rispondenti; }
    public Map<String, Double> getIndicatori() { return indicatori; }
    public void setIndicatori(Map<String, Double> indicatori) { this.indicatori = indicatori; }
}
